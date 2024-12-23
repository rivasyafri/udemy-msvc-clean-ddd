package com.food.ordering.system.order.service.domain.event;

import com.food.ordering.system.order.service.domain.entity.Order;
import id.rivasyafri.learning.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public abstract class OrderEvent implements DomainEvent<Order> {
  private final Order order;
  private final ZonedDateTime createdAt;

  protected OrderEvent(Order order,
                       ZonedDateTime createdAt) {
    this.order = order;
    this.createdAt = createdAt;
  }

  @Override
  public Order value() {
    return order;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }
}
