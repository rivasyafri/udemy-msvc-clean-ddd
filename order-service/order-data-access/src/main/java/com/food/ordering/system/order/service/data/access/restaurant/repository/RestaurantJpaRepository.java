package com.food.ordering.system.order.service.data.access.restaurant.repository;

import com.food.ordering.system.order.service.data.access.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.data.access.restaurant.entity.RestaurantEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {
  Optional<Set<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantId,
                                                                   Set<UUID> productId);
}
