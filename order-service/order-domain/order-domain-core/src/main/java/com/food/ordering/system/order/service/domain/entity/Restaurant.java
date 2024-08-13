package com.food.ordering.system.order.service.domain.entity;

import id.rivasyafri.learning.domain.entity.AggregateRoot;
import id.rivasyafri.learning.domain.value.objects.RestaurantId;

import java.util.List;

public class Restaurant extends AggregateRoot<RestaurantId> {
  private final List<Product> products;
  private boolean active;

  private Restaurant(Builder builder) {
    super.setId(builder.restaurantId);
    products = builder.products;
    active = builder.active;
  }


  public List<Product> getProducts() {
    return products;
  }

  public boolean isActive() {
    return active;
  }

  public static final class Builder {
    private RestaurantId restaurantId;
    private List<Product> products;
    private boolean active;

    private Builder() {
    }

    public static Builder builder() {
      return new Builder();
    }

    public Builder id(RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder products(List<Product> val) {
      products = val;
      return this;
    }

    public Builder active(boolean val) {
      active = val;
      return this;
    }

    public Restaurant build() {
      return new Restaurant(this);
    }
  }
}
