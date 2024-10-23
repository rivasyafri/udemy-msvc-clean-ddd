package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderPaymentEventKafkaPublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                         KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                         OrderServiceConfigData orderServiceConfigData,
                                         KafkaMessageHelper kafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                      BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {
    OrderPaymentEventPayload eventPayload =
        kafkaMessageHelper.getOrderEventPayload(orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);
    UUID sagaId = orderPaymentOutboxMessage.getSagaId();
    log.info(
        "Received {} for order id: {} and saga id: {}",
        orderPaymentOutboxMessage.getClass().getSimpleName(),
        eventPayload.getOrderId(),
        sagaId
    );
    try {
      PaymentRequestAvroModel paymentRequestAvroModel =
          orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(sagaId, eventPayload);
      kafkaProducer.send(
          orderServiceConfigData.getPaymentRequestTopicName(),
          sagaId.toString(),
          paymentRequestAvroModel,
          kafkaMessageHelper.successCallback(
              eventPayload.getOrderId().toString(),
              orderPaymentOutboxMessage,
              outboxCallback
          ),
          kafkaMessageHelper.failureCallback(
              orderServiceConfigData.getPaymentRequestTopicName(),
              paymentRequestAvroModel,
              orderPaymentOutboxMessage,
              outboxCallback,
              PaymentRequestAvroModel.class.getSimpleName()
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
