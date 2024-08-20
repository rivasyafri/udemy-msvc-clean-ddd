package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddressDto;
import com.food.ordering.system.order.service.domain.dto.create.OrderItemDto;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.value.objects.OrderItemId;
import com.food.ordering.system.order.service.domain.value.objects.StreetAddress;
import id.rivasyafri.learning.domain.value.objects.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {
  public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
    return Restaurant.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(
            createOrderCommand.getItems().stream()
                .map(OrderItemDto::getProductId)
                .map(ProductId::new)
                .map(Product::new)
                .collect(Collectors.toSet()).stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity()))
        ).build();
  }

  public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
    return Order.builder()
        .customerId(new CustomerId(createOrderCommand.getCustomerId()))
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
        .price(new Money(createOrderCommand.getPrice()))
        .items(orderItemDtoToOrderItemEntity(createOrderCommand.getItems()))
        .build();
  }

  public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
    return CreateOrderResponse.builder()
        .orderTrackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .message(message)
        .build();
  }

  public TrackOrderResponse orderToTrackOrderResponse(Order order) {
    return TrackOrderResponse.builder()
        .orderTrackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .failureMessages(order.getFailureMessages())
        .build();
  }

  private StreetAddress orderAddressToStreetAddress(OrderAddressDto orderAddressDto) {
    return new StreetAddress(
        UUIDv7.randomUUID(),
        orderAddressDto.getStreet(),
        orderAddressDto.getPostalCode(),
        orderAddressDto.getCity()
    );
  }

  private Set<OrderItem> orderItemDtoToOrderItemEntity(@NotNull Set<OrderItemDto> items) {
    return new HashSet<>(items.stream()
        .map(orderItemDto ->
            OrderItem.builder()
                .orderItemId(new OrderItemId(1L))
                .product(new Product(new ProductId(orderItemDto.getProductId())))
                .price(new Money(orderItemDto.getPrice()))
                .quantity(orderItemDto.getQuantity())
                .subTotal(new Money(orderItemDto.getSubTotal()))
                .build())
        .collect(Collectors.toMap(Function.identity(), Function.identity(), (left, right) -> OrderItem.builder()
            .orderItemId(left.getId())
            .price(!left.getPrice().isGreaterThan(right.getPrice()) ? left.getPrice() : right.getPrice())
            .quantity(left.getQuantity() + right.getQuantity())
            .subTotal(left.getSubTotal().add(right.getSubTotal()))
            .product(left.getProduct())
            .build()))
        .values());
  }
}
