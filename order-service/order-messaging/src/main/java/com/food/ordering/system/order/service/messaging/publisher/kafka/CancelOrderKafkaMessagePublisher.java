package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {
  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final KafkaMessageHelper kafkaMessageHelper;

  public CancelOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                          OrderServiceConfigData orderServiceConfigData,
                                          KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                          KafkaMessageHelper kafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaProducer = kafkaProducer;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(OrderCancelledEvent event) {
    String orderId = event.getOrder().getId().getValue().toString();
    log.info("Received {} for order id: {}", OrderCancelledEvent.class.getSimpleName(), orderId);

    try {
      PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(event);
      kafkaProducer.send(
          orderServiceConfigData.getPaymentRequestTopicName(),
          orderId,
          paymentRequestAvroModel,
          kafkaMessageHelper.successCallback(orderId),
          kafkaMessageHelper.failureCallback(
              orderServiceConfigData.getPaymentRequestTopicName(),
              paymentRequestAvroModel,
              PaymentRequestAvroModel.class.getSimpleName()
          )
      );

      log.info("{} sent to Kafka for order id: {}",
          PaymentRequestAvroModel.class.getSimpleName(), paymentRequestAvroModel.getOrderId());
    } catch (Exception e) {
      log.error("Error while sending {} message to Kafka with order id: {}, error: {}",
          PaymentRequestAvroModel.class.getSimpleName(),
          orderId,
          e.getMessage()
      );
    }
  }
}
