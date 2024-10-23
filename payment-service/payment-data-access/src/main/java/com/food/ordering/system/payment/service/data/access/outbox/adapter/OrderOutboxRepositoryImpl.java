package com.food.ordering.system.payment.service.data.access.outbox.adapter;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.data.access.outbox.exception.OrderOutboxNotFoundException;
import com.food.ordering.system.payment.service.data.access.outbox.mapper.OrderOutboxDataAccessMapper;
import com.food.ordering.system.payment.service.data.access.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {
  private final OrderOutboxJpaRepository orderOutboxJpaRepository;
  private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

  public OrderOutboxRepositoryImpl(OrderOutboxJpaRepository orderOutboxJpaRepository,
                                   OrderOutboxDataAccessMapper orderOutboxDataAccessMapper) {
    this.orderOutboxJpaRepository = orderOutboxJpaRepository;
    this.orderOutboxDataAccessMapper = orderOutboxDataAccessMapper;
  }

  @Override
  public OrderOutboxMessage save(OrderOutboxMessage orderPaymentOutboxMessage) {
    return orderOutboxDataAccessMapper
        .orderOutboxEntityToOrderOutboxMessage(
            orderOutboxJpaRepository.save(
                orderOutboxDataAccessMapper
                    .orderOutboxMessageToOutboxEntity(orderPaymentOutboxMessage)
            )
        );
  }

  @Override
  public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type,
                                                                      OutboxStatus outboxStatus) {
    return Optional.of(
        orderOutboxJpaRepository.findByTypeAndOutboxStatus(type, outboxStatus)
            .orElseThrow(() -> new OrderOutboxNotFoundException("Payment outbox object " +
                                                                    "could not be found for saga type " + type))
            .stream()
            .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage)
            .toList()
    );
  }

  @Override
  public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type,
                                                                                         UUID sagaId,
                                                                                         PaymentStatus paymentStatus,
                                                                                         OutboxStatus outboxStatus) {
    return orderOutboxJpaRepository
        .findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(type, sagaId, paymentStatus, outboxStatus)
        .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage);
  }

  @Override
  public void deleteByTypeAndOutboxStatus(String type,
                                          OutboxStatus outboxStatus) {
    orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
  }
}
