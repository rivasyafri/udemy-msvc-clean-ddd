package com.food.ordering.system.payment.service.domain.ports.output.repository;

import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import id.rivasyafri.learning.domain.value.objects.CustomerId;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {
  CreditHistory save(CreditHistory creditHistory);

  Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
