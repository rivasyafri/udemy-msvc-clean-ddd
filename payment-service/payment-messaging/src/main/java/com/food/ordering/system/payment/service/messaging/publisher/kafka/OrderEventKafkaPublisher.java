package com.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderEventKafkaPublisher implements PaymentResponseMessagePublisher {

  private final PaymentMessagingDataMapper paymentMessagingDataMapper;
  private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
  private final PaymentServiceConfigData paymentServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderEventKafkaPublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                  KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                  PaymentServiceConfigData paymentServiceConfigData,
                                  KafkaMessageHelper kafkaMessageHelper) {
    this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.paymentServiceConfigData = paymentServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(OrderOutboxMessage orderPaymentOutboxMessage,
                      BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
    OrderEventPayload eventPayload =
        kafkaMessageHelper.getOrderEventPayload(orderPaymentOutboxMessage.getPayload(), OrderEventPayload.class);
    UUID sagaId = orderPaymentOutboxMessage.getSagaId();
    log.info(
        "Received {} for order id: {} and saga id: {}",
        orderPaymentOutboxMessage.getClass().getSimpleName(),
        eventPayload.getOrderId(),
        sagaId
    );
    try {
      PaymentResponseAvroModel paymentResponseAvroModel =
          paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(sagaId, eventPayload);
      kafkaProducer.send(
          paymentServiceConfigData.getPaymentResponseTopicName(),
          sagaId.toString(),
          paymentResponseAvroModel,
          kafkaMessageHelper.successCallback(
              eventPayload.getOrderId().toString(),
              orderPaymentOutboxMessage,
              outboxCallback
          ),
          kafkaMessageHelper.failureCallback(
              paymentServiceConfigData.getPaymentResponseTopicName(),
              paymentResponseAvroModel,
              orderPaymentOutboxMessage,
              outboxCallback,
              PaymentResponseAvroModel.class.getSimpleName()
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
