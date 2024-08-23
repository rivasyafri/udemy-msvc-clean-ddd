package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {
  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final OrderKafkaMessageHelper orderKafkaMessageHelper;

  public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                          OrderServiceConfigData orderServiceConfigData,
                                          KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                          OrderKafkaMessageHelper orderKafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaProducer = kafkaProducer;
    this.orderKafkaMessageHelper = orderKafkaMessageHelper;
  }

  @Override
  public void publish(OrderCreatedEvent event) {
    String orderId = event.getOrder().getId().getValue().toString();
    log.info("Received {} for order id: {}", OrderCreatedEvent.class.getSimpleName(), orderId);

    try {
      PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(event);
      kafkaProducer.send(
          orderServiceConfigData.getPaymentRequestTopicName(),
          orderId,
          paymentRequestAvroModel,
          orderKafkaMessageHelper.successCallback(orderId),
          orderKafkaMessageHelper.failureCallback(
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
