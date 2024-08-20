package id.rivasyafri.learning.domain.event.publisher;

import id.rivasyafri.learning.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent<?>> {
  void publish(T event);
}
