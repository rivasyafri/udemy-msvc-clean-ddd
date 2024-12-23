package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import id.rivasyafri.learning.domain.event.DomainEvent;
import id.rivasyafri.learning.domain.value.objects.RestaurantId;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {
  private final OrderApproval orderApproval;
  private final RestaurantId restaurantId;
  private final List<String> failureMessages;
  private final ZonedDateTime createdAt;

  protected OrderApprovalEvent(OrderApproval orderApproval,
                               RestaurantId restaurantId,
                               List<String> failureMessages,
                               ZonedDateTime createdAt) {
    this.orderApproval = orderApproval;
    this.restaurantId = restaurantId;
    this.failureMessages = failureMessages;
    this.createdAt = createdAt;
  }

  @Override
  public OrderApproval value() {
    return orderApproval;
  }

  public RestaurantId getRestaurantId() {
    return restaurantId;
  }

  public List<String> getFailureMessages() {
    return failureMessages;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }
}
