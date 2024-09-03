package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import id.rivasyafri.learning.domain.value.objects.PaymentOrderStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class PaymentMessagingDataMapper {
  public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent) {
    return getPaymentResponseAvroModel(
        paymentCompletedEvent.getPayment(),
        paymentCompletedEvent.getCreatedAt(),
        paymentCompletedEvent.getFailureMessages()
    );
  }

  public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent) {
    return getPaymentResponseAvroModel(
        paymentCancelledEvent.getPayment(),
        paymentCancelledEvent.getCreatedAt(),
        paymentCancelledEvent.getFailureMessages()
    );
  }

  public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent) {
    return getPaymentResponseAvroModel(
        paymentFailedEvent.getPayment(),
        paymentFailedEvent.getCreatedAt(),
        paymentFailedEvent.getFailureMessages()
    );
  }

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

  private PaymentResponseAvroModel getPaymentResponseAvroModel(Payment payment,
                                                               ZonedDateTime createdAt,
                                                               List<String> failureMessages) {
    return PaymentResponseAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(UUIDv7.randomUUID())
        .setPaymentId(payment.getId().getValue())
        .setCustomerId(payment.getCustomerId().getValue())
        .setOrderId(payment.getOrderId().getValue())
        .setPrice(payment.getPrice().amount())
        .setCreatedAt(createdAt.toInstant())
        .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
        .setFailureMessages(failureMessages)
        .build();
  }
}
