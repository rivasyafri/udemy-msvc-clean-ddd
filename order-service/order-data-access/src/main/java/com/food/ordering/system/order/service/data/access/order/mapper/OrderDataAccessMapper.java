package com.food.ordering.system.order.service.data.access.order.mapper;

import com.food.ordering.system.order.service.data.access.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.data.access.order.entity.OrderEntity;
import com.food.ordering.system.order.service.data.access.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.value.objects.OrderItemId;
import com.food.ordering.system.order.service.domain.value.objects.StreetAddress;
import com.food.ordering.system.order.service.domain.value.objects.TrackingId;
import id.rivasyafri.learning.domain.value.objects.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Component
public class OrderDataAccessMapper {
  public OrderEntity orderToOrderEntity(Order order) {
    OrderEntity orderEntity = OrderEntity.builder()
        .id(order.getId().getValue())
        .customerId(order.getCustomerId().getValue())
        .restaurantId(order.getRestaurantId().getValue())
        .trackingId(order.getTrackingId().getValue())
        .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
        .price(order.getPrice().amount())
        .items(orderItemsToOrderItemEntities(order.getItems()))
        .orderStatus(order.getOrderStatus())
        .failureMessages(CollectionUtils.isEmpty(order.getFailureMessages())
            ? String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages())
            : "")
        .build();
    orderEntity.getAddress().setOrder(orderEntity);
    orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    return orderEntity;
  }

  public Order orderEntityToOrder(OrderEntity orderEntity) {
    return Order.builder()
        .orderId(new OrderId(orderEntity.getId()))
        .customerId(new CustomerId(orderEntity.getCustomerId()))
        .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
        .trackingId(new TrackingId(orderEntity.getTrackingId()))
        .price(new Money(orderEntity.getPrice()))
        .orderStatus(orderEntity.getOrderStatus())
        .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
        .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
        .failureMessages(orderEntity.getFailureMessages().isBlank()
            ? new ArrayList<>()
            : new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))))
        .build();
  }

  private Set<OrderItem> orderItemEntitiesToOrderItems(Set<OrderItemEntity> items) {
    return items.stream()
        .map(orderItemEntity -> OrderItem.builder()
            .orderItemId(new OrderItemId(orderItemEntity.getId()))
            .product(new Product(new ProductId(orderItemEntity.getProductId())))
            .price(new Money(orderItemEntity.getPrice()))
            .quantity(orderItemEntity.getQuantity())
            .subTotal(new Money(orderItemEntity.getSubTotal()))
            .build())
        .collect(Collectors.toSet());
  }

  private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
    return new StreetAddress(address.getId(), address.getStreet(), address.getPostalCode(), address.getCity());
  }

  private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
    return OrderAddressEntity.builder()
        .id(deliveryAddress.id())
        .street(deliveryAddress.street())
        .postalCode(deliveryAddress.postalCode())
        .city(deliveryAddress.city())
        .build();
  }

  private Set<OrderItemEntity> orderItemsToOrderItemEntities(Set<OrderItem> items) {
    return items.stream()
        .map(orderItem -> OrderItemEntity.builder()
            .id(orderItem.getId().getValue())
            .productId(orderItem.getProduct().getId().getValue())
            .price(orderItem.getPrice().amount())
            .quantity(orderItem.getQuantity())
            .subTotal(orderItem.getSubTotal().amount())
            .build())
        .collect(Collectors.toSet());
  }
}
