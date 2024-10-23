package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
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
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {
  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final OrderDataMapper orderDataMapper;

  public OrderApprovalSaga(OrderDomainService orderDomainService,
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
  public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
    Optional<OrderApprovalOutboxMessage> outboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            restaurantApprovalResponse.getSagaId(),
            SagaStatus.PROCESSING
        );
    if (outboxMessageResponse.isEmpty()) {
      log.info("An outbox message with saga id: {} is already processed!", restaurantApprovalResponse.getSagaId());
      return;
    }
    OrderApprovalOutboxMessage outboxMessage = outboxMessageResponse.get();
    Order order = approveOrder(restaurantApprovalResponse);
    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
    approvalOutboxHelper.save(
        getUpdatedApprovalOutboxMessage(outboxMessage, order.getOrderStatus(), sagaStatus)
    );
    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(restaurantApprovalResponse.getSagaId(), order.getOrderStatus(), sagaStatus)
    );
    log.info("Order with id: {} is approved", order.getId().getValue());
  }

  @Override
  @Transactional
  public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
    Optional<OrderApprovalOutboxMessage> outboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            restaurantApprovalResponse.getSagaId(),
            SagaStatus.PROCESSING
        );
    if (outboxMessageResponse.isEmpty()) {
      log.info("An outbox message with saga id: {} is already roll-backed!", restaurantApprovalResponse.getSagaId());
      return;
    }
    OrderApprovalOutboxMessage outboxMessage = outboxMessageResponse.get();
    OrderCancelledEvent domainEvent = rollbackOrder(restaurantApprovalResponse);
    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.value().getOrderStatus());
    approvalOutboxHelper.save(
        getUpdatedApprovalOutboxMessage(outboxMessage, domainEvent.value().getOrderStatus(), sagaStatus)
    );
    paymentOutboxHelper.save(
        orderDataMapper.orderPaymentEventPayloadToOrderPaymentOutboxMessage(
            orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(domainEvent),
            domainEvent.value().getOrderStatus(),
            sagaStatus,
            OutboxStatus.STARTED,
            restaurantApprovalResponse.getSagaId()
        )
    );
    log.info("Order with id: {} is cancelling", domainEvent.value().getId().getValue());
  }

  private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
    orderDomainService.approveOrder(order);
    orderSagaHelper.saveOrder(order);
    return order;
  }

  private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage outboxMessage,
                                                                     OrderStatus orderStatus,
                                                                     SagaStatus sagaStatus) {
    outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    outboxMessage.setOrderStatus(orderStatus);
    outboxMessage.setSagaStatus(sagaStatus);
    return outboxMessage;
  }

  private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(UUID sagaId,
                                                                   OrderStatus orderStatus,
                                                                   SagaStatus sagaStatus) {
    Optional<OrderPaymentOutboxMessage> outboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            sagaId,
            SagaStatus.PROCESSING
        );
    if (outboxMessageResponse.isEmpty()) {
      throw new OrderDomainException("Payment outbox message cannot be found in " + SagaStatus.PROCESSING.name() + " "
                                         + "state");
    }
    OrderPaymentOutboxMessage orderPaymentOutboxMessage = outboxMessageResponse.get();
    orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
    return orderPaymentOutboxMessage;
  }

  private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
    OrderCancelledEvent domainEvent = orderDomainService.cancelOrderPayment(
        order,
        restaurantApprovalResponse.getFailureMessages()
    );
    orderSagaHelper.saveOrder(order);
    return domainEvent;
  }
}
