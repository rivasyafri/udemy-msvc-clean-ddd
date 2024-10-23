package com.food.ordering.system.payment.service.data.access.outbox.mapper;

import com.food.ordering.system.payment.service.data.access.outbox.entity.OrderOutboxEntity;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxDataAccessMapper {
  public OrderOutboxEntity orderOutboxMessageToOutboxEntity(OrderOutboxMessage outboxMessage) {
    return OrderOutboxEntity.builder()
        .id(outboxMessage.getId())
        .sagaId(outboxMessage.getSagaId())
        .createdAt(outboxMessage.getCreatedAt())
        .type(outboxMessage.getType())
        .payload(outboxMessage.getPayload())
        .paymentStatus(outboxMessage.getPaymentStatus())
        .outboxStatus(outboxMessage.getOutboxStatus())
        .version(outboxMessage.getVersion())
        .build();
  }

  public OrderOutboxMessage orderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity outboxEntity) {
    return OrderOutboxMessage.builder()
        .id(outboxEntity.getId())
        .sagaId(outboxEntity.getSagaId())
        .createdAt(outboxEntity.getCreatedAt())
        .type(outboxEntity.getType())
        .payload(outboxEntity.getPayload())
        .paymentStatus(outboxEntity.getPaymentStatus())
        .outboxStatus(outboxEntity.getOutboxStatus())
        .version(outboxEntity.getVersion())
        .build();
  }
}
