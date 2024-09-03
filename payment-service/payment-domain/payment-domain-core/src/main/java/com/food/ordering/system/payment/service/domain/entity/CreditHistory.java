package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.payment.service.domain.value.object.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.value.object.TransactionType;
import id.rivasyafri.learning.domain.entity.BaseEntity;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import id.rivasyafri.learning.domain.value.objects.Money;

public class CreditHistory extends BaseEntity<CreditHistoryId> {
  private final CustomerId customerId;
  private final Money amount;
  private final TransactionType transactionType;

  private CreditHistory(Builder builder) {
    setId(builder.creditHistoryId);
    customerId = builder.customerId;
    amount = builder.amount;
    transactionType = builder.transactionType;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public Money getAmount() {
    return amount;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private CreditHistoryId creditHistoryId;
    private CustomerId customerId;
    private Money amount;
    private TransactionType transactionType;

    private Builder() {
    }

    public Builder creditHistoryId(CreditHistoryId val) {
      creditHistoryId = val;
      return this;
    }

    public Builder customerId(CustomerId val) {
      customerId = val;
      return this;
    }

    public Builder amount(Money val) {
      amount = val;
      return this;
    }

    public Builder transactionType(TransactionType val) {
      transactionType = val;
      return this;
    }

    public CreditHistory build() {
      return new CreditHistory(this);
    }
  }
}
