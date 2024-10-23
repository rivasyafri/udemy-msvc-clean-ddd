package id.rivasyafri.learning.domain.event;

public interface DomainEvent<T> {
  T value();
}
