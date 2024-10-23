package com.food.ordering.system.payment.service.domain.mapper;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import id.rivasyafri.learning.domain.value.objects.Money;
import id.rivasyafri.learning.domain.value.objects.OrderId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataMapper {

  public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
    return Payment.builder()
        .orderId(new OrderId(paymentRequest.getOrderId()))
        .customerId(new CustomerId(paymentRequest.getCustomerId()))
        .price(new Money(paymentRequest.getPrice()))
        .build();
  }

  public OrderEventPayload paymentEventToOrderEventPayload(PaymentEvent paymentEvent) {
    return OrderEventPayload.builder()
        .paymentId(paymentEvent.value().getId().getValue())
        .customerId(paymentEvent.value().getCustomerId().getValue())
        .orderId(paymentEvent.value().getOrderId().getValue())
        .price(paymentEvent.value().getPrice().amount())
        .createdAt(paymentEvent.getCreatedAt())
        .paymentStatus(paymentEvent.value().getPaymentStatus().name())
        .failureMessages(paymentEvent.getFailureMessages())
        .build();
  }
}
