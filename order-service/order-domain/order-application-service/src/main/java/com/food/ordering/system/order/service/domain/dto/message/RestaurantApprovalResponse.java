package com.food.ordering.system.order.service.domain.dto.message;

import id.rivasyafri.learning.domain.value.objects.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalResponse {
  private UUID id;
  private UUID sagaId;
  private UUID orderId;
  private UUID restaurantId;
  private Instant createdAt;
  private OrderApprovalStatus orderApprovalStatus;
  private List<String> failureMessages;
}
