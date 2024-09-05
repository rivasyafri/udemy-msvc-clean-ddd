package com.food.ordering.system.restaurant.service.data.access.restaurant.entity;

import id.rivasyafri.learning.domain.value.objects.OrderApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_approval", schema = "restaurant")
@Entity
public class OrderApprovalEntity {

  @Id
  private UUID id;
  private UUID restaurantId;
  private UUID orderId;
  @Enumerated(EnumType.STRING)
  private OrderApprovalStatus status;
}
