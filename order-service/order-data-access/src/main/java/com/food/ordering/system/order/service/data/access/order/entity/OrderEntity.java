package com.food.ordering.system.order.service.data.access.order.entity;

import id.rivasyafri.learning.domain.value.objects.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class OrderEntity {
  @Id
  private UUID id;
  private UUID customerId;
  private UUID restaurantId;
  private UUID trackingId;
  private BigDecimal price;
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;
  private String failureMessages;
  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private OrderAddressEntity address;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<OrderItemEntity> items;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderEntity that = (OrderEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
