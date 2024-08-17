package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {
  private final OrderDomainService orderDomainService;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final RestaurantRepository restaurantRepository;
  private final OrderDataMapper orderDataMapper;

  public OrderCreateHelper(OrderDomainService orderDomainService,
                           OrderRepository orderRepository,
                           CustomerRepository customerRepository,
                           RestaurantRepository restaurantRepository,
                           OrderDataMapper orderDataMapper) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.restaurantRepository = restaurantRepository;
    this.orderDataMapper = orderDataMapper;
  }

  @Transactional
  public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
    checkCustomerExists(createOrderCommand.getCustomerId());
    Restaurant restaurant = checkRestaurantExists(createOrderCommand);
    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
    Order orderSaved = saveOrder(order);
    log.info("Saved order with id: {}", orderSaved.getId().getValue());
    return orderCreatedEvent;
  }

  private void checkCustomerExists(@NotNull UUID customerId) {
    customerRepository.findCustomer(customerId).orElseThrow(() -> {
      log.warn("Could not find customer with id: {}", customerId);
      return new OrderDomainException("Could not find customer with id: " + customerId);
    });
  }

  private Restaurant checkRestaurantExists(CreateOrderCommand createOrderCommand) {
    Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
    return restaurantRepository.findRestaurantInformation(restaurant)
        .orElseThrow(() -> {
          log.warn("Could not find restaurant with id: {}", createOrderCommand.getRestaurantId());
          return new OrderDomainException("Could not find restaurant with id: " + createOrderCommand.getRestaurantId());
        });
  }

  private Order saveOrder(Order order) {
    Order orderSaved = orderRepository.save(order);
    if (orderSaved == null) {
      log.error("Could not save order from customer: {}, and restaurant: {}", order.getCustomerId(), order.getRestaurantId());
      throw new OrderDomainException("Could not save order");
    }
    return orderSaved;
  }
}
