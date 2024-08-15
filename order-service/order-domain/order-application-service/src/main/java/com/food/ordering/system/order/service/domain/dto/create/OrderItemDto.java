package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemDto {
  @NotNull
  private final UUID productId;
  @NotNull
  @Min(value = 1)
  private final Integer quantity;
  @NotNull
  @Min(value = 0)
  private final BigDecimal price;
  @NotNull
  @Min(value = 0)
  private final BigDecimal subTotal;
}
