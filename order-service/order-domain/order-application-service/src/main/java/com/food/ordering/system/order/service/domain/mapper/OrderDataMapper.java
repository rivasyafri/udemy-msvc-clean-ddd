package com.food.ordering.system.order.service.domain.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddressDto;
import com.food.ordering.system.order.service.domain.dto.create.OrderItemDto;
import com.food.ordering.system.order.service.domain.dto.message.CustomerModel;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.*;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.value.objects.OrderItemId;
import com.food.ordering.system.order.service.domain.value.objects.StreetAddress;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import id.rivasyafri.learning.domain.value.objects.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.food.ordering.system.saga.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class OrderDataMapper {
  private final ObjectMapper objectMapper;

  public OrderDataMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
    return Restaurant.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(
            createOrderCommand.getItems().stream()
                .map(OrderItemDto::getProductId)
                .map(ProductId::new)
                .map(Product::new)
                .collect(Collectors.toSet()).stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity()))
        ).build();
  }

  public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
    return Order.builder()
        .customerId(new CustomerId(createOrderCommand.getCustomerId()))
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
        .price(new Money(createOrderCommand.getPrice()))
        .items(orderItemDtoToOrderItemEntity(createOrderCommand.getItems()))
        .build();
  }

  public CreateOrderResponse orderToCreateOrderResponse(Order order,
                                                        String message) {
    return CreateOrderResponse.builder()
        .orderTrackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .message(message)
        .build();
  }

  public TrackOrderResponse orderToTrackOrderResponse(Order order) {
    return TrackOrderResponse.builder()
        .orderTrackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .failureMessages(order.getFailureMessages())
        .build();
  }

  public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent) {
    return OrderPaymentEventPayload.builder()
        .customerId(orderCreatedEvent.value().getCustomerId().getValue())
        .orderId(orderCreatedEvent.value().getId().getValue())
        .price(orderCreatedEvent.value().getPrice().amount())
        .createdAt(orderCreatedEvent.getCreatedAt())
        .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
        .build();
  }

  public OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {
    return OrderApprovalEventPayload.builder()
        .orderId(orderPaidEvent.value().getId().getValue())
        .restaurantId(orderPaidEvent.value().getRestaurantId().getValue())
        .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
        .products(
            orderPaidEvent.value().getItems()
                .stream()
                .map(orderItem ->
                         OrderApprovalEventProduct.builder()
                             .id(orderItem.getProduct().getId().getValue())
                             .quantity(orderItem.getQuantity())
                             .build()
                )
                .toList()
        )
        .price(orderPaidEvent.value().getPrice().amount())
        .createdAt(orderPaidEvent.getCreatedAt())
        .build();
  }

  public OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent) {
    return OrderPaymentEventPayload.builder()
        .customerId(orderCancelledEvent.value().getCustomerId().getValue())
        .orderId(orderCancelledEvent.value().getId().getValue())
        .price(orderCancelledEvent.value().getPrice().amount())
        .createdAt(orderCancelledEvent.getCreatedAt())
        .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
        .build();
  }

  private StreetAddress orderAddressToStreetAddress(OrderAddressDto orderAddressDto) {
    return new StreetAddress(
        UUIDv7.randomUUID(),
        orderAddressDto.getStreet(),
        orderAddressDto.getPostalCode(),
        orderAddressDto.getCity()
    );
  }

  private Set<OrderItem> orderItemDtoToOrderItemEntity(@NotNull Set<OrderItemDto> items) {
    return new HashSet<>(items.stream()
                             .map(orderItemDto ->
                                      OrderItem.builder()
                                          .orderItemId(new OrderItemId(1L))
                                          .product(new Product(new ProductId(orderItemDto.getProductId())))
                                          .price(new Money(orderItemDto.getPrice()))
                                          .quantity(orderItemDto.getQuantity())
                                          .subTotal(new Money(orderItemDto.getSubTotal()))
                                          .build())
                             .collect(Collectors.toMap(Function.identity(), Function.identity(),
                                                       (left, right) -> OrderItem
                                                           .builder()
                                                           .orderItemId(left.getId())
                                                           .price(!left.getPrice()
                                                               .isGreaterThan(right.getPrice()) ? left.getPrice() :
                                                                      right.getPrice())
                                                           .quantity(left.getQuantity() + right.getQuantity())
                                                           .subTotal(left.getSubTotal().add(right.getSubTotal()))
                                                           .product(left.getProduct())
                                                           .build()
                             ))
                             .values());
  }

  public OrderApprovalOutboxMessage orderApprovalEventPayloadToOrderApprovalOutboxMessage(OrderApprovalEventPayload orderApprovalEventPayload,
                                                                                          OrderStatus orderStatus,
                                                                                          SagaStatus sagaStatus,
                                                                                          OutboxStatus outboxStatus,
                                                                                          UUID sagaId) {
    return OrderApprovalOutboxMessage.builder()
        .id(UUIDv7.randomUUID())
        .sagaId(sagaId)
        .type(ORDER_SAGA_NAME)
        .createdAt(orderApprovalEventPayload.getCreatedAt())
        .payload(createPayload(orderApprovalEventPayload))
        .orderStatus(orderStatus)
        .sagaStatus(sagaStatus)
        .outboxStatus(outboxStatus)
        .build();
  }

  public OrderPaymentOutboxMessage orderPaymentEventPayloadToOrderPaymentOutboxMessage(OrderPaymentEventPayload orderPaymentEventPayload,
                                                                                       OrderStatus orderStatus,
                                                                                       SagaStatus sagaStatus,
                                                                                       OutboxStatus outboxStatus,
                                                                                       UUID sagaId) {
    return OrderPaymentOutboxMessage.builder()
        .id(UUIDv7.randomUUID())
        .sagaId(sagaId)
        .type(ORDER_SAGA_NAME)
        .createdAt(orderPaymentEventPayload.getCreatedAt())
        .payload(createPayload(orderPaymentEventPayload))
        .orderStatus(orderStatus)
        .sagaStatus(sagaStatus)
        .outboxStatus(outboxStatus)
        .build();
  }

  public Customer customerModelToCustomer(CustomerModel customerModel) {
    return new Customer(
        new CustomerId(customerModel.getId()),
        customerModel.getUsername(),
        customerModel.getFirstName(),
        customerModel.getLastName()
    );
  }


  private String createPayload(OrderApprovalEventPayload payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("Could not create {} object for order id: {}", payload.getClass()
          .getSimpleName(), payload.getOrderId(), e);
      throw new OrderDomainException("Could not create " + payload.getClass()
          .getSimpleName() + " object for order id: " + payload.getOrderId(), e);
    }
  }

  private String createPayload(OrderPaymentEventPayload payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("Could not create {} object for order id: {}", payload.getClass()
          .getSimpleName(), payload.getOrderId(), e);
      throw new OrderDomainException("Could not create " + payload.getClass()
          .getSimpleName() + " object for order id: " + payload.getOrderId(), e);
    }
  }
}
