package com.food.ordering.system.payment.service.domain.ports.output.repository;

import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import id.rivasyafri.learning.domain.value.objects.CustomerId;

import java.util.Optional;

public interface CreditEntryRepository {
  CreditEntry save(CreditEntry creditEntry);

  Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
