package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import id.rivasyafri.learning.domain.value.objects.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {
  private final OrderRepository orderRepository;

  public OrderSagaHelper(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Order findOrder(UUID orderId) {
    Optional<Order> orderResponse = orderRepository.findById(new OrderId(orderId));
    if (orderResponse.isEmpty()) {
      log.error("Order with id {} not found", orderId);
      throw new OrderNotFoundException("Order with id " + orderId + " not found");
    }
    return orderResponse.get();
  }

  void saveOrder(Order order) {
    orderRepository.save(order);
  }
}
