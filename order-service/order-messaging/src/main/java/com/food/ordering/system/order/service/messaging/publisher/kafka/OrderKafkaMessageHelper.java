package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class OrderKafkaMessageHelper {
  public <T> Function<Throwable, Void> failureCallback(String responseTopicName, T requestAvroModel, String requestAvroModelName) {
    return throwable -> {
      log.error("Error while sending {}: message {} to topic {}",
          requestAvroModelName, requestAvroModel.toString(), responseTopicName, throwable);
      return null;
    };
  }

  public <T> Consumer<SendResult<String, T>> successCallback(String orderId) {
    return result -> {
      RecordMetadata recordMetadata = result.getRecordMetadata();
      log.info("Received successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
          orderId,
          recordMetadata.topic(),
          recordMetadata.partition(),
          recordMetadata.offset(),
          recordMetadata.timestamp());
    };
  }
}
