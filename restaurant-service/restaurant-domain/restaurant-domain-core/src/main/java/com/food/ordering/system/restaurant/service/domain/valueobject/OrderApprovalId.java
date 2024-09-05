package com.food.ordering.system.restaurant.service.domain.valueobject;

import id.rivasyafri.learning.domain.value.objects.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
  public OrderApprovalId(UUID value) {
    super(value);
  }
}
