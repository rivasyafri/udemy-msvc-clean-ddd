package com.food.ordering.system.payment.service.domain.ports.output.repository;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {
  OrderOutboxMessage save(OrderOutboxMessage orderPaymentOutboxMessage);

  Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type,
                                                               OutboxStatus outboxStatus);

  Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type,
                                                                                  UUID sagaId,
                                                                                  PaymentStatus paymentStatus,
                                                                                  OutboxStatus outboxStatus);

  void deleteByTypeAndOutboxStatus(String type,
                                   OutboxStatus outboxStatus);
}