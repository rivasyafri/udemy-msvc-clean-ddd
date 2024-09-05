package com.food.ordering.system.restaurant.service.messaging.mapper;


import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import id.rivasyafri.learning.domain.value.objects.ProductId;
import id.rivasyafri.learning.domain.value.objects.RestaurantOrderStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantMessagingDataMapper {
  public RestaurantApprovalResponseAvroModel
  orderApprovedEventToRestaurantApprovalResponseAvroModel(OrderApprovedEvent orderApprovedEvent) {
    return RestaurantApprovalResponseAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(UUIDv7.randomUUID())
        .setOrderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue())
        .setRestaurantId(orderApprovedEvent.getRestaurantId().getValue())
        .setCreatedAt(orderApprovedEvent.getCreatedAt().toInstant())
        .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderApprovedEvent.
            getOrderApproval().getApprovalStatus().name()))
        .setFailureMessages(orderApprovedEvent.getFailureMessages())
        .build();
  }

  public RestaurantApprovalResponseAvroModel
  orderRejectedEventToRestaurantApprovalResponseAvroModel(OrderRejectedEvent orderRejectedEvent) {
    return RestaurantApprovalResponseAvroModel.newBuilder()
        .setId(UUIDv7.randomUUID())
        .setSagaId(UUIDv7.randomUUID())
        .setOrderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue())
        .setRestaurantId(orderRejectedEvent.getRestaurantId().getValue())
        .setCreatedAt(orderRejectedEvent.getCreatedAt().toInstant())
        .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderRejectedEvent.
            getOrderApproval().getApprovalStatus().name()))
        .setFailureMessages(orderRejectedEvent.getFailureMessages())
        .build();
  }

  public RestaurantApprovalRequest
  restaurantApprovalRequestAvroModelToRestaurantApproval(RestaurantApprovalRequestAvroModel
                                                             restaurantApprovalRequestAvroModel) {
    return RestaurantApprovalRequest.builder()
        .id(restaurantApprovalRequestAvroModel.getId())
        .sagaId(restaurantApprovalRequestAvroModel.getSagaId())
        .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId())
        .orderId(restaurantApprovalRequestAvroModel.getOrderId())
        .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantApprovalRequestAvroModel
            .getRestaurantOrderStatus().name()))
        .products(restaurantApprovalRequestAvroModel.getProducts()
            .stream().map(avroModel ->
                Product.builder()
                    .productId(new ProductId(UUID.fromString(avroModel.getId())))
                    .quantity(avroModel.getQuantity())
                    .build())
            .toList())
        .price(restaurantApprovalRequestAvroModel.getPrice())
        .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
        .build();
  }
}
