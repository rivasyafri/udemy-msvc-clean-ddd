package com.food.ordering.system.payment.service.domain.mapper;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.Payment;
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
}
