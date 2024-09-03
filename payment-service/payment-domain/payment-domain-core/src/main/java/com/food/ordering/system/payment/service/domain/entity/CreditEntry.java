package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.payment.service.domain.value.object.CreditEntryId;
import id.rivasyafri.learning.domain.entity.BaseEntity;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import id.rivasyafri.learning.domain.value.objects.Money;

public class CreditEntry extends BaseEntity<CreditEntryId> {
  private final CustomerId customerId;
  private Money totalCreditAmount;

  public void addCreditAmount(Money amount) {
    totalCreditAmount = totalCreditAmount.add(amount);
  }

  public void subtractCreditAmount(Money amount) {
    totalCreditAmount = totalCreditAmount.subtract(amount);
  }

  private CreditEntry(Builder builder) {
    setId(builder.creditEntryId);
    customerId = builder.customerId;
    totalCreditAmount = builder.totalCreditAmount;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public Money getTotalCreditAmount() {
    return totalCreditAmount;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private CreditEntryId creditEntryId;
    private CustomerId customerId;
    private Money totalCreditAmount;

    private Builder() {
    }

    public Builder creditEntryId(CreditEntryId val) {
      creditEntryId = val;
      return this;
    }

    public Builder customerId(CustomerId val) {
      customerId = val;
      return this;
    }

    public Builder totalCreditAmount(Money val) {
      totalCreditAmount = val;
      return this;
    }

    public CreditEntry build() {
      return new CreditEntry(this);
    }
  }
}
