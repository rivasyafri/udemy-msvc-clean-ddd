package com.food.ordering.system.payment.service.domain.value.object;

import id.rivasyafri.learning.domain.value.objects.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
  public CreditEntryId(UUID value) {
    super(value);
  }
}
