package com.food.ordering.system.payment.service.domain.value.object;

import id.rivasyafri.learning.domain.value.objects.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
  public PaymentId(UUID value) {
    super(value);
  }
}
