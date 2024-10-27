package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;
import id.rivasyafri.learning.domain.entity.BaseEntity;
import id.rivasyafri.learning.domain.value.objects.OrderApprovalStatus;
import id.rivasyafri.learning.domain.value.objects.OrderId;
import id.rivasyafri.learning.domain.value.objects.RestaurantId;

import java.util.Objects;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
  private final RestaurantId restaurantId;
  private final OrderId orderId;
  private final OrderApprovalStatus approvalStatus;

  private OrderApproval(Builder builder) {
    setId(builder.orderApprovalId);
    restaurantId = builder.restaurantId;
    orderId = builder.orderId;
    approvalStatus = builder.approvalStatus;
  }

  public static Builder builder() {
    return new Builder();
  }


  public RestaurantId getRestaurantId() {
    return restaurantId;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public OrderApprovalStatus getApprovalStatus() {
    return approvalStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OrderApproval that = (OrderApproval) o;
    return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(orderId, that.orderId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), restaurantId, orderId);
  }

  public static final class Builder {
    private OrderApprovalId orderApprovalId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private OrderApprovalStatus approvalStatus;

    private Builder() {
    }

    public Builder orderApprovalId(OrderApprovalId val) {
      orderApprovalId = val;
      return this;
    }

    public Builder restaurantId(RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder orderId(OrderId val) {
      orderId = val;
      return this;
    }

    public Builder approvalStatus(OrderApprovalStatus val) {
      approvalStatus = val;
      return this;
    }

    public OrderApproval build() {
      return new OrderApproval(this);
    }
  }
}
