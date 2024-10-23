package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import id.rivasyafri.learning.domain.value.objects.PaymentOrderStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {
  public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
    return PaymentRequest.builder()
        .id(paymentRequestAvroModel.getId())
        .sagaId(paymentRequestAvroModel.getSagaId())
        .customerId(paymentRequestAvroModel.getCustomerId())
        .orderId(paymentRequestAvroModel.getOrderId())
        .price(paymentRequestAvroModel.getPrice())
        .createdAt(paymentRequestAvroModel.getCreatedAt())
        .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
        .build();
  }

  public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(UUID sagaId,
                                                                              OrderEventPayload eventPayload) {
    return PaymentResponseAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(sagaId)
        .setPaymentId(eventPayload.getPaymentId())
        .setCustomerId(eventPayload.getCustomerId())
        .setOrderId(eventPayload.getOrderId())
        .setPrice(eventPayload.getPrice())
        .setCreatedAt(eventPayload.getCreatedAt().toInstant())
        .setPaymentStatus(PaymentStatus.valueOf(eventPayload.getPaymentStatus()))
        .setFailureMessages(eventPayload.getFailureMessages())
        .build();
  }
}
