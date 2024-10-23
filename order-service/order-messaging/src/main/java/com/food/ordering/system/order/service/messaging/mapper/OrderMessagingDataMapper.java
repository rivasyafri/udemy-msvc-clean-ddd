package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import id.rivasyafri.learning.domain.value.objects.OrderApprovalStatus;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {
  public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
    Order order = orderCreatedEvent.value();
    return PaymentRequestAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(null)
        .setCustomerId(order.getCustomerId().getValue())
        .setOrderId(order.getId().getValue())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
        .build();
  }

  public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
    Order order = orderCancelledEvent.value();
    return PaymentRequestAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(null)
        .setCustomerId(order.getCustomerId().getValue())
        .setOrderId(order.getId().getValue())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
        .build();
  }

  public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
    Order order = orderPaidEvent.value();
    return RestaurantApprovalRequestAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(null)
        .setOrderId(order.getId().getValue())
        .setRestaurantId(order.getRestaurantId().getValue())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
        .setProducts(order.getItems().stream()
                         .map(orderItem -> Product.newBuilder()
                             .setId(orderItem.getProduct().getId().getValue().toString())
                             .setQuantity(orderItem.getQuantity())
                             .build())
                         .toList())
        .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
        .build();
  }

  public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {
    return PaymentResponse.builder()
        .id(paymentResponseAvroModel.getId())
        .sagaId(paymentResponseAvroModel.getSagaId())
        .paymentId(paymentResponseAvroModel.getPaymentId())
        .customerId(paymentResponseAvroModel.getCustomerId())
        .orderId(paymentResponseAvroModel.getOrderId())
        .price(paymentResponseAvroModel.getPrice())
        .createdAt(paymentResponseAvroModel.getCreatedAt())
        .paymentStatus(PaymentStatus.valueOf(paymentResponseAvroModel.getPaymentStatus().name()))
        .failureMessages(paymentResponseAvroModel.getFailureMessages())
        .build();
  }

  public RestaurantApprovalResponse approvalResponseAvroModelToApprovalResponse(RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel) {
    return RestaurantApprovalResponse.builder()
        .id(restaurantApprovalResponseAvroModel.getId())
        .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
        .orderId(restaurantApprovalResponseAvroModel.getOrderId())
        .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
        .createdAt(restaurantApprovalResponseAvroModel.getCreatedAt())
        .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
        .orderApprovalStatus(OrderApprovalStatus.valueOf(restaurantApprovalResponseAvroModel.getOrderApprovalStatus()
                                                             .name()))
        .build();
  }

  public PaymentRequestAvroModel orderPaymentEventToPaymentRequestAvroModel(UUID sagaId,
                                                                            OrderPaymentEventPayload orderPaymentEventPayload) {
    return PaymentRequestAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(sagaId)
        .setCustomerId(orderPaymentEventPayload.getCustomerId())
        .setOrderId(orderPaymentEventPayload.getOrderId())
        .setPrice(orderPaymentEventPayload.getPrice())
        .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
        .build();
  }

  public RestaurantApprovalRequestAvroModel orderApprovalEventToRestaurantApprovalRequestAvroModel(UUID sagaId,
                                                                                                   OrderApprovalEventPayload orderApprovalEventPayload) {
    return RestaurantApprovalRequestAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(sagaId)
        .setOrderId(orderApprovalEventPayload.getOrderId())
        .setRestaurantId(orderApprovalEventPayload.getRestaurantId())
        .setRestaurantOrderStatus(RestaurantOrderStatus.valueOf(orderApprovalEventPayload.getRestaurantOrderStatus()))
        .setProducts(
            orderApprovalEventPayload.getProducts()
                .stream()
                .map(orderApprovalEventProduct -> Product.newBuilder()
                    .setId(String.valueOf(orderApprovalEventProduct.getId()))
                    .setQuantity(orderApprovalEventProduct.getQuantity())
                    .build()
                )
                .toList())
        .setPrice(orderApprovalEventPayload.getPrice())
        .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
        .build();
  }
}
