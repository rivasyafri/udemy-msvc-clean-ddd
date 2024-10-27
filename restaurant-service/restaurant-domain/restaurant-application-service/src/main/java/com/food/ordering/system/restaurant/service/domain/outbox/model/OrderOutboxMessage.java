package com.food.ordering.system.restaurant.service.domain.outbox.model;

import com.food.ordering.system.outbox.OutboxStatus;
import id.rivasyafri.learning.domain.value.objects.OrderApprovalStatus;
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
  private OutboxStatus outboxStatus;
  private OrderApprovalStatus approvalStatus;
  private int version;

  public void setOutboxStatus(OutboxStatus status) {
    this.outboxStatus = status;
  }
}
