package com.food.ordering.system.payment.service.data.access.credit.entry.adapter;

import com.food.ordering.system.payment.service.data.access.credit.entry.mapper.CreditEntryDataAccessMapper;
import com.food.ordering.system.payment.service.data.access.credit.entry.repository.CreditEntryJpaRepository;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {
  private final CreditEntryJpaRepository creditEntryJpaRepository;
  private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

  public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                   CreditEntryDataAccessMapper creditEntryDataAccessMapper) {
    this.creditEntryJpaRepository = creditEntryJpaRepository;
    this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
  }

  @Override
  public CreditEntry save(CreditEntry creditEntry) {
    return creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(
        creditEntryJpaRepository.save(
            creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)
        )
    );
  }

  @Override
  public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
    return creditEntryJpaRepository.findByCustomerId(customerId.getValue())
        .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
  }
}
