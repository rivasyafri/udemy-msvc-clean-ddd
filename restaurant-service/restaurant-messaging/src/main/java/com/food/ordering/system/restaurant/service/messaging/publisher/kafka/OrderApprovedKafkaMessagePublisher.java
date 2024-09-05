package com.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderApprovedKafkaMessagePublisher implements OrderApprovedMessagePublisher {

  private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
  private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
  private final RestaurantServiceConfigData restaurantServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderApprovedKafkaMessagePublisher(RestaurantMessagingDataMapper restaurantMessagingDataMapper,
                                            KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer,
                                            RestaurantServiceConfigData restaurantServiceConfigData,
                                            KafkaMessageHelper kafkaMessageHelper) {
    this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.restaurantServiceConfigData = restaurantServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(OrderApprovedEvent event) {
    String orderId = event.getOrderApproval().getOrderId().getValue().toString();

      log.info("Received {} for order id: {}", event.getClass().getSimpleName(), orderId);

    try {
      RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
          restaurantMessagingDataMapper
              .orderApprovedEventToRestaurantApprovalResponseAvroModel(event);

      kafkaProducer.send(
          restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
          orderId,
          restaurantApprovalResponseAvroModel,
          kafkaMessageHelper.successCallback(orderId),
          kafkaMessageHelper.failureCallback(
              restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
              restaurantApprovalResponseAvroModel,
              RestaurantApprovalResponseAvroModel.class.getSimpleName())
      );
      log.info("{} sent to kafka at: {}",
          RestaurantApprovalResponseAvroModel.class.getSimpleName(), System.nanoTime());
    } catch (Exception e) {
      log.error("Error while sending {} message to kafka with order id: {}, error: {}",
          RestaurantApprovalResponseAvroModel.class.getSimpleName(),
          orderId,
          e.getMessage());
    }
  }

}
