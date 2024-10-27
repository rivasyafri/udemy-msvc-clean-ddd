package com.food.ordering.system.order.service.data.access.restaurant.mapper;

import com.food.ordering.system.order.service.data.access.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.data.access.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import id.rivasyafri.learning.domain.entity.BaseEntity;
import id.rivasyafri.learning.domain.value.objects.BaseId;
import id.rivasyafri.learning.domain.value.objects.Money;
import id.rivasyafri.learning.domain.value.objects.ProductId;
import id.rivasyafri.learning.domain.value.objects.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {
  public Set<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
    return restaurant.getProducts().values().stream()
        .map(BaseEntity::getId)
        .map(BaseId::getValue)
        .collect(Collectors.toSet());
  }

  public Restaurant restaurantEntitiesToRestaurant(Set<RestaurantEntity> restaurantEntities) {
    RestaurantEntity restaurantEntity = restaurantEntities.stream().findFirst()
        .orElseThrow(() -> new RestaurantDataAccessException("Restaurant could not be found!"));
    Map<Product, Product> restaurantProducts = restaurantEntities.stream()
        .map(entity -> new Product(
            new ProductId(entity.getProductId()),
            entity.getProductName(),
            new Money(entity.getProductPrice())
        ))
        .collect(Collectors.toMap(Function.identity(), Function.identity()));
    return Restaurant.builder()
        .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
        .products(restaurantProducts)
        .active(restaurantEntity.getRestaurantActive())
        .build();
  }
}
