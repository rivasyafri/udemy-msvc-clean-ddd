package com.food.ordering.system.restaurant.service.domain.dto;

import com.food.ordering.system.restaurant.service.domain.entity.Product;
import id.rivasyafri.learning.domain.value.objects.RestaurantOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {
  private UUID id;
  private UUID sagaId;
  private UUID restaurantId;
  private UUID orderId;
  private RestaurantOrderStatus restaurantOrderStatus;
  private java.util.List<Product> products;
  private java.math.BigDecimal price;
  private java.time.Instant createdAt;
}
