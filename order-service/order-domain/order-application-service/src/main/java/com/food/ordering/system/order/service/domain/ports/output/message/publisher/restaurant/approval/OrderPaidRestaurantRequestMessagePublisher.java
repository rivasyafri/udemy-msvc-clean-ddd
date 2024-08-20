package com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.approval;

import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
