package com.food.ordering.system.order.service.domain.outbox.model.approval;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import id.rivasyafri.learning.domain.value.objects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderApprovalOutboxMessage {
  private UUID id;
  private UUID sagaId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String type;
  private String payload;
  private SagaStatus sagaStatus;
  private OrderStatus orderStatus;
  private OutboxStatus outboxStatus;
  private int version;

  public void setProcessedAt(ZonedDateTime processedAt) {
    this.processedAt = processedAt;
  }

  public void setSagaStatus(SagaStatus sagaStatus) {
    this.sagaStatus = sagaStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public void setOutboxStatus(OutboxStatus outboxStatus) {
    this.outboxStatus = outboxStatus;
  }
}
