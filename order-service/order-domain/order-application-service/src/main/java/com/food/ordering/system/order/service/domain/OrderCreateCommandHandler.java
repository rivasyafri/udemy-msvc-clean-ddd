package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderCreateCommandHandler {
  private final OrderCreateHelper orderCreateHelper;
  private final OrderDataMapper orderDataMapper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final OrderSagaHelper orderSagaHelper;

  public OrderCreateCommandHandler(
      OrderCreateHelper orderCreateHelper,
      OrderDataMapper orderDataMapper,
      PaymentOutboxHelper paymentOutboxHelper,
      OrderSagaHelper orderSagaHelper
  ) {
    this.orderCreateHelper = orderCreateHelper;
    this.orderDataMapper = orderDataMapper;
    this.paymentOutboxHelper = paymentOutboxHelper;
    this.orderSagaHelper = orderSagaHelper;
  }

  @Transactional
  public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
    OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
    CreateOrderResponse createOrderResponse = orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.value(),
                                                                                         "Order Created Successfully");
    paymentOutboxHelper.save(
        orderDataMapper.orderPaymentEventPayloadToOrderPaymentOutboxMessage(
            orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
            orderCreatedEvent.value().getOrderStatus(),
            orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.value().getOrderStatus()),
            OutboxStatus.STARTED,
            UUIDv7.randomUUID()
        )
    );
    return createOrderResponse;
  }
}
