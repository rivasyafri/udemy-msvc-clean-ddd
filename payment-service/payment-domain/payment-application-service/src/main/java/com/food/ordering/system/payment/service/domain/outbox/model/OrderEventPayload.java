package com.food.ordering.system.payment.service.domain.outbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderEventPayload {
  @JsonProperty
  private UUID paymentId;
  @JsonProperty
  private UUID customerId;
  @JsonProperty
  private UUID orderId;
  @JsonProperty
  private BigDecimal price;
  @JsonProperty
  private ZonedDateTime createdAt;
  @JsonProperty
  private String paymentStatus;
  @JsonProperty
  private List<String> failureMessages;
}