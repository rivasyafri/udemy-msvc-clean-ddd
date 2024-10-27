package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.value.objects.TrackingId;
import id.rivasyafri.learning.domain.value.objects.OrderId;

import java.util.Optional;

public interface OrderRepository {
  Order save(Order order);

  Optional<Order> findById(OrderId orderId);

  Optional<Order> findByTrackingId(TrackingId trackingId);
}
