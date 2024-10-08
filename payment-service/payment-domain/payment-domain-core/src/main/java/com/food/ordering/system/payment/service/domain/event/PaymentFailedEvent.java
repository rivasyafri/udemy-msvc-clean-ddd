package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.payment.service.domain.entity.Payment;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {
  private final DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher;
  public PaymentFailedEvent(Payment payment,
                            ZonedDateTime createdAt,
                            List<String> failureMessages,
                            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher) {
    super(payment, createdAt, failureMessages);
    this.paymentFailedEventPublisher = paymentFailedEventPublisher;
  }

  @Override
  public void fire() {
    paymentFailedEventPublisher.publish(this);
  }
}
