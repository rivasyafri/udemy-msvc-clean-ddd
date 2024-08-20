package com.food.ordering.system.order.service.domain.value.objects;

import id.rivasyafri.learning.domain.value.objects.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
  public TrackingId(UUID value) {
    super(value);
  }
}
