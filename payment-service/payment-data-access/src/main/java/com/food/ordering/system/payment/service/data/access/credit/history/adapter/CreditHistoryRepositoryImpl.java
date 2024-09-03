package com.food.ordering.system.payment.service.data.access.credit.history.adapter;

import com.food.ordering.system.payment.service.data.access.credit.history.entity.CreditHistoryEntity;
import com.food.ordering.system.payment.service.data.access.credit.history.mapper.CreditHistoryDataAccessMapper;
import com.food.ordering.system.payment.service.data.access.credit.history.repository.CreditHistoryJpaRepository;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {
  private final CreditHistoryJpaRepository creditHistoryJpaRepository;
  private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

  public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository,
                                     CreditHistoryDataAccessMapper creditHistoryDataAccessMapper) {
    this.creditHistoryJpaRepository = creditHistoryJpaRepository;
    this.creditHistoryDataAccessMapper = creditHistoryDataAccessMapper;
  }

  @Override
  public CreditHistory save(CreditHistory creditHistory) {
    return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(
        creditHistoryJpaRepository.save(
            creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)
        )
    );
  }

  @Override
  public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
    Optional<List< CreditHistoryEntity>> creditHistories =
        creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
    return creditHistories
        .map(creditHistoryEntities -> creditHistoryEntities.stream()
            .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
            .collect(Collectors.toList())
        );
  }
}
