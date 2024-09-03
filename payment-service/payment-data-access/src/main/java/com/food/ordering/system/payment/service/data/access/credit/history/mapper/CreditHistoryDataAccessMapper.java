package com.food.ordering.system.payment.service.data.access.credit.history.mapper;

import com.food.ordering.system.payment.service.data.access.credit.history.entity.CreditHistoryEntity;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.value.object.CreditHistoryId;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import id.rivasyafri.learning.domain.value.objects.Money;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {
  public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
    return CreditHistoryEntity.builder()
        .id(creditHistory.getId().getValue())
        .customerId(creditHistory.getCustomerId().getValue())
        .amount(creditHistory.getAmount().amount())
        .type(creditHistory.getTransactionType())
        .build();
  }

  public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
    return CreditHistory.builder()
        .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
        .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
        .amount(new Money(creditHistoryEntity.getAmount()))
        .transactionType(creditHistoryEntity.getType())
        .build();
  }
}
