package com.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
