package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {
  @NotNull
  private final UUID customerId;
  @NotNull
  private final UUID restaurantId;
  @NotNull
  @Min(value = 0)
  private final BigDecimal price;
  @NotNull
  private final Set<OrderItemDto> items;
  @NotNull
  private final OrderAddressDto address;
}
