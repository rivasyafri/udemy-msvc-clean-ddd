package com.food.ordering.system.order.service.domain.event;

import com.food.ordering.system.order.service.domain.entity.Order;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {
  private final DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher;
  public OrderCancelledEvent(Order order,
                             ZonedDateTime createdAt,
                             DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher) {
    super(order, createdAt);
    this.orderCancelledEventDomainEventPublisher = orderCancelledEventDomainEventPublisher;
  }

  @Override
  public void fire() {
    orderCancelledEventDomainEventPublisher.publish(this);
  }
}
