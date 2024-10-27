package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import com.food.ordering.system.saga.SagaStep;
import id.rivasyafri.learning.domain.value.objects.OrderStatus;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static id.rivasyafri.learning.domain.DomainConstants.UTC;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {
  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final OrderDataMapper orderDataMapper;

  public OrderPaymentSaga(OrderDomainService orderDomainService,
                          OrderSagaHelper orderSagaHelper,
                          PaymentOutboxHelper paymentOutboxHelper,
                          ApprovalOutboxHelper approvalOutboxHelper,
                          OrderDataMapper orderDataMapper) {
    this.orderDomainService = orderDomainService;
    this.orderSagaHelper = orderSagaHelper;
    this.paymentOutboxHelper = paymentOutboxHelper;
    this.approvalOutboxHelper = approvalOutboxHelper;
    this.orderDataMapper = orderDataMapper;
  }

  @Override
  @Transactional
  public void process(PaymentResponse paymentResponse) {
    Optional<OrderPaymentOutboxMessage> outboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            paymentResponse.getSagaId(),
            SagaStatus.STARTED
        );

    if (outboxMessageResponse.isEmpty()) {
      log.info("An outbox message with saga id: {} is already processed!", paymentResponse.getSagaId());
      return;
    }

    OrderPaymentOutboxMessage outboxMessage = outboxMessageResponse.get();

    OrderPaidEvent domainEvent = completePaymentForOrder(paymentResponse);

    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.value().getOrderStatus());

    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            outboxMessage, domainEvent.value().getOrderStatus(), sagaStatus
        )
    );

    approvalOutboxHelper.save(
        orderDataMapper.orderApprovalEventPayloadToOrderApprovalOutboxMessage(
            orderDataMapper.orderPaidEventToOrderApprovalEventPayload(domainEvent),
            domainEvent.value().getOrderStatus(),
            sagaStatus,
            OutboxStatus.STARTED,
            paymentResponse.getSagaId()
        )
    );

    log.info("Payment completed for order with id: {}", paymentResponse.getOrderId());
  }

  @Override
  @Transactional
  public void rollback(PaymentResponse paymentResponse) {
    Optional<OrderPaymentOutboxMessage> outboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            paymentResponse.getSagaId(),
            getCurrentSagaStatus(paymentResponse.getPaymentStatus())
        );

    if (outboxMessageResponse.isEmpty()) {
      log.info("An outbox message with saga id: {} is already roll backed!", paymentResponse.getSagaId());
      return;
    }

    OrderPaymentOutboxMessage outboxMessage = outboxMessageResponse.get();
    Order order = rollbackPaymentForOrder(paymentResponse);

    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            outboxMessage,
            order.getOrderStatus(),
            sagaStatus
        ));

    if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
      approvalOutboxHelper.save(
          getUpdatedApprovalOutboxMessage(
              paymentResponse.getSagaId(),
              order.getOrderStatus(),
              sagaStatus
          ));
    }
    log.info("Order with id: {} is cancelled", paymentResponse.getOrderId());
  }

  private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(UUID sagaId,
                                                                     OrderStatus orderStatus,
                                                                     SagaStatus sagaStatus) {
    Optional<OrderApprovalOutboxMessage> outboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            sagaId,
            SagaStatus.COMPENSATING
        );
    if (outboxMessageResponse.isEmpty()) {
      throw new OrderDomainException("Approval outbox message could not be found in " + SagaStatus.COMPENSATING.name() + " status");
    }
    OrderApprovalOutboxMessage outboxMessage = outboxMessageResponse.get();
    outboxMessage.setOrderStatus(orderStatus);
    outboxMessage.setSagaStatus(sagaStatus);
    outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    return outboxMessage;
  }

  private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage outboxMessage,
                                                                   OrderStatus orderStatus,
                                                                   SagaStatus sagaStatus) {
    outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    outboxMessage.setOrderStatus(orderStatus);
    outboxMessage.setSagaStatus(sagaStatus);
    return outboxMessage;
  }

  private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
    log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
    OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
    orderSagaHelper.saveOrder(order);
    return domainEvent;
  }

  private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
    return switch (paymentStatus) {
      case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
      case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
      case FAILED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};
    };
  }

  private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
    log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
    orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
    orderSagaHelper.saveOrder(order);
    return order;
  }
}
