package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.approval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {
  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
  private final OrderKafkaMessageHelper orderKafkaMessageHelper;

  public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                       OrderServiceConfigData orderServiceConfigData,
                                       KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                       OrderKafkaMessageHelper orderKafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaProducer = kafkaProducer;
    this.orderKafkaMessageHelper = orderKafkaMessageHelper;
  }

  @Override
  public void publish(OrderPaidEvent event) {
    String orderId = event.getOrder().getId().getValue().toString();

    try {
      RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel = orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(event);

      kafkaProducer.send(
          orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
          orderId,
          restaurantApprovalRequestAvroModel,
          orderKafkaMessageHelper.successCallback(orderId),
          orderKafkaMessageHelper.failureCallback(
              orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
              restaurantApprovalRequestAvroModel,
              RestaurantApprovalRequestAvroModel.class.getSimpleName()
          )
      );

      log.info("{} sent to kafka for order id: {}",
          RestaurantApprovalRequestAvroModel.class.getSimpleName(),
          orderId
      );
    } catch (Exception e) {
      log.error("Error while sending {} message to kafka with order id: {}, error: {}",
          RestaurantApprovalRequestAvroModel.class.getSimpleName(),
          orderId,
          e.getMessage()
      );
    }
  }
}
