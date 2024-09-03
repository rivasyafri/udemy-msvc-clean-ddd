package com.food.ordering.system.payment.service.domain.value.object;

import id.rivasyafri.learning.domain.value.objects.BaseId;

import java.util.UUID;

public class CreditHistoryId extends BaseId<UUID> {
  public CreditHistoryId(UUID value) {
    super(value);
  }
}
