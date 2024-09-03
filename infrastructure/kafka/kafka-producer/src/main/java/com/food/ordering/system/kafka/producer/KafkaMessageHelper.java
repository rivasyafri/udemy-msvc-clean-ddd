package com.food.ordering.system.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class KafkaMessageHelper {
  public <T> Function<Throwable, Void> failureCallback(String topicName, T avroModel, String avroModelName) {
    return throwable -> {
      log.error("Error while sending {}: message {} to topic {}",
          avroModelName, avroModel.toString(), topicName, throwable);
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
