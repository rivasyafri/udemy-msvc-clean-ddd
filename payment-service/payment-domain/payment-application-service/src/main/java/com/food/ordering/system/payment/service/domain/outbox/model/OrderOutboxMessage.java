package com.food.ordering.system.payment.service.domain.outbox.model;

import com.food.ordering.system.outbox.OutboxStatus;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderOutboxMessage {
  private UUID id;
  private UUID sagaId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String type;
  private String payload;
  private PaymentStatus paymentStatus;
  private OutboxStatus outboxStatus;
  private int version;

  public void setOutboxStatus(OutboxStatus outboxStatus) {
    this.outboxStatus = outboxStatus;
  }
}
