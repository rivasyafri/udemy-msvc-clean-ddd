package com.food.ordering.system.payment.service.data.access.credit.entry.mapper;

import com.food.ordering.system.payment.service.data.access.credit.entry.entity.CreditEntryEntity;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.value.object.CreditEntryId;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import id.rivasyafri.learning.domain.value.objects.Money;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {
  public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
    return CreditEntry.builder()
        .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
        .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
        .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
        .build();
  }

  public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
    return CreditEntryEntity.builder()
        .id(creditEntry.getId().getValue())
        .customerId(creditEntry.getCustomerId().getValue())
        .totalCreditAmount(creditEntry.getTotalCreditAmount().amount())
        .build();
  }
}
