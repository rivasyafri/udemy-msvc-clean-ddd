package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static id.rivasyafri.learning.domain.DomainConstants.UTC;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService{

  @Override
  public OrderCreatedEvent validateAndInitiateOrder(Order order,
                                                    Restaurant restaurant,
                                                    DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher) {
    validateRestaurant(restaurant);
    setOrderProductInformation(order, restaurant);
    order.validateOrder();
    order.initializeOrder();
    log.info("Order with id: {} is initiated", order.getId().getValue());
    return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderCreatedEventPublisher);
  }

  @Override
  public OrderPaidEvent payOrder(Order order,
                                 DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
    order.pay();
    log.info("Order with id: {} paid successfully", order.getId().getValue());
    return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderPaidEventDomainEventPublisher);
  }

  @Override
  public void approveOrder(Order order) {
    order.approve();
    log.info("Order with id: {} approved", order.getId().getValue());
  }

  @Override
  public OrderCancelledEvent cancelOrderPayment(Order order,
                                                List<String> failureMessages,
                                                DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher) {
    order.initCancel(failureMessages);
    log.info("Order payment is cancelling for order with id: {}", order.getId().getValue());
    return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderCancelledEventDomainEventPublisher);
  }

  @Override
  public void cancelOrder(Order order, List<String> failureMessages) {
    order.cancel(failureMessages);
    log.info("Order with id: {} is cancelled", order.getId().getValue());
  }

  private void validateRestaurant(Restaurant restaurant) {
    if (!restaurant.isActive()) {
      throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() +
          " is currently not active!");
    }
  }

  private void setOrderProductInformation(Order order, Restaurant restaurant) {
    order.getItems().stream().filter(Objects::nonNull).forEach(item -> {
      Product currentProduct = item.getProduct();
      Product productFromRestaurant = restaurant.getProducts().get(currentProduct);
      if (Objects.nonNull(productFromRestaurant)) {
        currentProduct.updateWithConfirmedNameAndPrice(
            productFromRestaurant.getName(),
            productFromRestaurant.getPrice()
        );
      }
    });
  }
}
