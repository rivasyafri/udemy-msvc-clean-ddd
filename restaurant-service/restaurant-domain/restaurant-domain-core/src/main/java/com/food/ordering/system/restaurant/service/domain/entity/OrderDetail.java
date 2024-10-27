package com.food.ordering.system.restaurant.service.domain.entity;

import id.rivasyafri.learning.domain.entity.BaseEntity;
import id.rivasyafri.learning.domain.value.objects.Money;
import id.rivasyafri.learning.domain.value.objects.OrderId;
import id.rivasyafri.learning.domain.value.objects.OrderStatus;

import java.util.List;

public class OrderDetail extends BaseEntity<OrderId> {
  private final List<Product> products;
  private OrderStatus orderStatus;
  private Money totalAmount;

  private OrderDetail(Builder builder) {
    setId(builder.orderId);
    orderStatus = builder.orderStatus;
    totalAmount = builder.totalAmount;
    products = builder.products;
  }

  public static Builder builder() {
    return new Builder();
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public Money getTotalAmount() {
    return totalAmount;
  }

  public List<Product> getProducts() {
    return products;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public static final class Builder {
    private OrderId orderId;
    private OrderStatus orderStatus;
    private Money totalAmount;
    private List<Product> products;

    private Builder() {
    }

    public Builder orderId(OrderId val) {
      orderId = val;
      return this;
    }

    public Builder orderStatus(OrderStatus val) {
      orderStatus = val;
      return this;
    }

    public Builder totalAmount(Money val) {
      totalAmount = val;
      return this;
    }

    public Builder products(List<Product> val) {
      products = val;
      return this;
    }

    public OrderDetail build() {
      return new OrderDetail(this);
    }
  }
}
