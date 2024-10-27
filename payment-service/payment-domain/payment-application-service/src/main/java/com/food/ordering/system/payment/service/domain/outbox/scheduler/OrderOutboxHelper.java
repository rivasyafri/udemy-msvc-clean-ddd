package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.exception.PaymentDomainException;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.saga.SagaConstants.ORDER_SAGA_NAME;
import static id.rivasyafri.learning.domain.DomainConstants.UTC;

@Slf4j
@Component
public class OrderOutboxHelper {
  private final OrderOutboxRepository orderOutboxRepository;
  private final ObjectMapper objectMapper;

  public OrderOutboxHelper(OrderOutboxRepository orderOutboxRepository,
                           ObjectMapper objectMapper) {
    this.orderOutboxRepository = orderOutboxRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(readOnly = true)
  public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(UUID sagaId,
                                                                                             PaymentStatus paymentStatus) {
    return orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        ORDER_SAGA_NAME,
        sagaId,
        paymentStatus,
        OutboxStatus.COMPLETED
    );
  }

  @Transactional(readOnly = true)
  public Optional<List<OrderOutboxMessage>> getOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
    return orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
  }

  @Transactional
  public void deleteOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
    orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
  }

  @Transactional
  public void save(OrderOutboxMessage orderOutboxMessage) {
    OrderOutboxMessage outboxMessage = orderOutboxRepository.save(orderOutboxMessage);
    if (outboxMessage == null) {
      log.error("Could not save {} with outbox id: {}", OrderOutboxMessage.class.getSimpleName(),
                orderOutboxMessage.getId());
      throw new PaymentDomainException("Could not save " + OrderOutboxMessage.class.getSimpleName() + " with outbox " +
                                           "id: " +
                                           orderOutboxMessage.getId());
    }
    log.info("OrderOutboxMessage saved with outbox id: {}", outboxMessage.getId());
  }

  @Transactional
  public void save(OrderEventPayload eventPayload,
                   PaymentStatus paymentStatus,
                   OutboxStatus outboxStatus,
                   UUID sagaId) {
    this.save(OrderOutboxMessage.builder()
                  .id(UUIDv7.randomUUID())
                  .sagaId(sagaId)
                  .type(ORDER_SAGA_NAME)
                  .createdAt(eventPayload.getCreatedAt())
                  .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                  .payload(createPayload(eventPayload))
                  .paymentStatus(paymentStatus)
                  .outboxStatus(outboxStatus)
                  .build());
  }

  @Transactional
  public void updateOutboxStatus(OrderOutboxMessage outboxMessage,
                                 OutboxStatus outboxStatus) {
    outboxMessage.setOutboxStatus(outboxStatus);
    save(outboxMessage);
    log.info("{} is updated with outbox status: {} ", outboxMessage.getClass().getSimpleName(), outboxStatus.name());
  }

  private String createPayload(OrderEventPayload payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("Could not create {} object for order id: {}", payload.getClass()
          .getSimpleName(), payload.getOrderId(), e);
      throw new PaymentDomainException("Could not create " + payload.getClass()
          .getSimpleName() + " object for order id: " + payload.getOrderId(), e);
    }
  }
}
