package com.food.ordering.system.payment.service.data.access.payment.mapper;

import com.food.ordering.system.payment.service.data.access.payment.entity.PaymentEntity;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.value.object.PaymentId;
import id.rivasyafri.learning.domain.value.objects.CustomerId;
import id.rivasyafri.learning.domain.value.objects.Money;
import id.rivasyafri.learning.domain.value.objects.OrderId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {
  public PaymentEntity paymentToPaymentEntity(Payment payment) {
    return PaymentEntity.builder()
        .id(payment.getId().getValue())
        .customerId(payment.getCustomerId().getValue())
        .orderId(payment.getOrderId().getValue())
        .price(payment.getPrice().amount())
        .status(payment.getPaymentStatus())
        .createdAt(payment.getCreatedAt())
        .build();
  }

  public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
    return Payment.builder()
        .paymentId(new PaymentId(paymentEntity.getId()))
        .customerId(new CustomerId(paymentEntity.getCustomerId()))
        .orderId(new OrderId(paymentEntity.getOrderId()))
        .price(new Money(paymentEntity.getPrice()))
        .createdAt(paymentEntity.getCreatedAt())
        .build();
  }
}
