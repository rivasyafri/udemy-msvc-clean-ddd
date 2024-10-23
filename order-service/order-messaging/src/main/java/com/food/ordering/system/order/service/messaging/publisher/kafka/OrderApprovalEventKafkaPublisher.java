package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.approval.RestaurantApprovalRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderApprovalEventKafkaPublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                          KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                          OrderServiceConfigData orderServiceConfigData,
                                          KafkaMessageHelper kafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                      BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback) {
    OrderApprovalEventPayload eventPayload =
        kafkaMessageHelper.getOrderEventPayload(orderApprovalOutboxMessage.getPayload(),
                                                OrderApprovalEventPayload.class);
    UUID sagaId = orderApprovalOutboxMessage.getSagaId();
    log.info(
        "Received {} for order id: {} and saga id: {}",
        orderApprovalOutboxMessage.getClass().getSimpleName(),
        eventPayload.getOrderId(),
        sagaId
    );
    try {
      RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
          orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId, eventPayload);
      kafkaProducer.send(
          orderServiceConfigData.getPaymentRequestTopicName(),
          sagaId.toString(),
          restaurantApprovalRequestAvroModel,
          kafkaMessageHelper.successCallback(
              eventPayload.getOrderId().toString(),
              orderApprovalOutboxMessage,
              outboxCallback
          ),
          kafkaMessageHelper.failureCallback(
              orderServiceConfigData.getPaymentRequestTopicName(),
              restaurantApprovalRequestAvroModel,
              orderApprovalOutboxMessage,
              outboxCallback,
              RestaurantApprovalRequestAvroModel.class.getSimpleName()
          )
      );
      log.info(
          "{} sent to Kafka for order id: {} and saga id: {}",
          eventPayload.getClass().getSimpleName(),
          eventPayload.getOrderId(),
          sagaId
      );
    } catch (Exception e) {
      log.error(
          "Error while sending {} to kafka with order id: {} and saga id: {}, error: {}",
          eventPayload.getClass().getSimpleName(),
          eventPayload.getOrderId(),
          sagaId,
          e.getMessage()
      );
    }
  }
}
