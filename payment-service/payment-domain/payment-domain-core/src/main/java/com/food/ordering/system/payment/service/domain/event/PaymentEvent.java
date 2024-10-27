package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.payment.service.domain.entity.Payment;
import id.rivasyafri.learning.domain.event.DomainEvent;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class PaymentEvent implements DomainEvent<Payment> {
  private final Payment payment;
  private final ZonedDateTime createdAt;
  private final List<String> failureMessages;

  protected PaymentEvent(Payment payment,
                         ZonedDateTime createdAt,
                         List<String> failureMessages) {
    this.payment = payment;
    this.createdAt = createdAt;
    this.failureMessages = failureMessages;
  }

  @Override
  public Payment value() {
    return payment;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public List<String> getFailureMessages() {
    return failureMessages;
  }
}
