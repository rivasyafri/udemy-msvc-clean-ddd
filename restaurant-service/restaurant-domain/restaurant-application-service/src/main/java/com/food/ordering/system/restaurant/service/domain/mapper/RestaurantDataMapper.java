package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import id.rivasyafri.learning.domain.value.objects.Money;
import id.rivasyafri.learning.domain.value.objects.OrderId;
import id.rivasyafri.learning.domain.value.objects.OrderStatus;
import id.rivasyafri.learning.domain.value.objects.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
  public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest
                                                              restaurantApprovalRequest) {
    return Restaurant.builder()
        .restaurantId(new RestaurantId(restaurantApprovalRequest.getRestaurantId()))
        .orderDetail(OrderDetail.builder()
            .orderId(new OrderId(restaurantApprovalRequest.getOrderId()))
            .products(restaurantApprovalRequest.getProducts().stream().map(
                    product -> Product.builder()
                        .productId(product.getId())
                        .quantity(product.getQuantity())
                        .build())
                .collect(Collectors.toList()))
            .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
            .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
            .build())
        .build();
  }
}
