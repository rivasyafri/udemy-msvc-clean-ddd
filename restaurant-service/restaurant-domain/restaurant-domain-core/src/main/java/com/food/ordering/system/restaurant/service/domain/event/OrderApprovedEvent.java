package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;
import id.rivasyafri.learning.domain.value.objects.RestaurantId;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {

  private final DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;

  public OrderApprovedEvent(OrderApproval orderApproval,
                            RestaurantId restaurantId,
                            List<String> failureMessages,
                            ZonedDateTime createdAt,
                            DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher) {
    super(orderApproval, restaurantId, failureMessages, createdAt);
    this.orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher;
  }

  @Override
  public void fire() {
    orderApprovedEventDomainEventPublisher.publish(this);
  }
}
