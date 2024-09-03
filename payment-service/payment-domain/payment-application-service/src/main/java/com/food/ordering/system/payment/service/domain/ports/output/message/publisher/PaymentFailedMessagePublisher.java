package com.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import id.rivasyafri.learning.domain.event.publisher.DomainEventPublisher;

public interface PaymentFailedMessagePublisher extends DomainEventPublisher<PaymentFailedEvent> {
}
