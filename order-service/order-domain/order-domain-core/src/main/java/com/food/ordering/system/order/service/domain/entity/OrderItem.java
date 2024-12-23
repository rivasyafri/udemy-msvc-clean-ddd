package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.order.service.domain.value.objects.OrderItemId;
import id.rivasyafri.learning.domain.entity.BaseEntity;
import id.rivasyafri.learning.domain.value.objects.Money;
import id.rivasyafri.learning.domain.value.objects.OrderId;

public class OrderItem extends BaseEntity<OrderItemId> {
  private final Product product;
  private final int quantity;
  private final Money price;
  private final Money subTotal;
  private OrderId orderId;

  private OrderItem(Builder builder) {
    super.setId(builder.orderItemId);
    product = builder.product;
    quantity = builder.quantity;
    price = builder.price;
    subTotal = builder.subTotal;
  }

  public static Builder builder() {
    return new Builder();
  }

  void initializeOrderItem(OrderId orderId,
                           OrderItemId orderItemId) {
    this.orderId = orderId;
    super.setId(orderItemId);
  }

  boolean isPriceValid() {
    return price.isGreaterThanZero() &&
        price.equals(product.getPrice()) &&
        price.multiply(quantity).equals(subTotal);
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public Money getPrice() {
    return price;
  }

  public Money getSubTotal() {
    return subTotal;
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
    private OrderItemId orderItemId;
    private Product product;
    private int quantity;
    private Money price;
    private Money subTotal;

    private Builder() {
    }

    public Builder orderItemId(OrderItemId val) {
      orderItemId = val;
      return this;
    }

    public Builder product(Product val) {
      product = val;
      return this;
    }

    public Builder quantity(int val) {
      quantity = val;
      return this;
    }

    public Builder price(Money val) {
      price = val;
      return this;
    }

    public Builder subTotal(Money val) {
      subTotal = val;
      return this;
    }

    public OrderItem build() {
      return new OrderItem(this);
    }
  }
}
