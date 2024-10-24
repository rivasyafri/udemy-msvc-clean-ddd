package com.food.ordering.system.payment.service.data.access.outbox.repository;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.data.access.outbox.entity.OrderOutboxEntity;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {
  Optional<List<OrderOutboxEntity>> findByTypeAndOutboxStatus(String type,
                                                              OutboxStatus outboxStatus);

  Optional<OrderOutboxEntity> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type,
                                                                                 UUID sagaId,
                                                                                 PaymentStatus paymentStatus,
                                                                                 OutboxStatus outboxStatus);

  void deleteByTypeAndOutboxStatus(String type,
                                   OutboxStatus outboxStatus);
}
