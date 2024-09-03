package com.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCompletedKafkaMessagePublisher implements PaymentCompletedMessagePublisher {
  private final PaymentMessagingDataMapper paymentMessagingDataMapper;
  private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
  private final PaymentServiceConfigData paymentServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public PaymentCompletedKafkaMessagePublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                               KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                               PaymentServiceConfigData paymentServiceConfigData,
                                               KafkaMessageHelper kafkaMessageHelper) {
    this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.paymentServiceConfigData = paymentServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(PaymentCompletedEvent event) {
    String orderId = event.getPayment().getOrderId().getValue().toString();
    log.info("Received {} for order id: {}", event.getClass().getSimpleName(), orderId);
    try {
      PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(event);
      kafkaProducer.send(
          paymentServiceConfigData.getPaymentResponseTopicName(),
          orderId,
          paymentResponseAvroModel,
          kafkaMessageHelper.successCallback(orderId),
          kafkaMessageHelper.failureCallback(
              paymentServiceConfigData.getPaymentRequestTopicName(),
              paymentResponseAvroModel,
              PaymentResponseAvroModel.class.getSimpleName())
      );
      log.info("{} sent to Kafka for order id: {}",
          PaymentResponseAvroModel.class.getSimpleName(), paymentResponseAvroModel.getOrderId());
    } catch (Exception e) {
      log.error("Error while sending {} message to Kafka with order id: {}, error: {}",
          PaymentResponseAvroModel.class.getSimpleName(),
          orderId,
          e.getMessage()
      );
    }
  }
}
