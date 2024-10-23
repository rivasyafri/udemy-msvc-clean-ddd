package com.food.ordering.system.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class KafkaMessageHelper {
  private final ObjectMapper objectMapper;

  public KafkaMessageHelper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public <T, U> Function<Throwable, Void> failureCallback(String topicName,
                                                          T avroModel,
                                                          U outboxMessage,
                                                          BiConsumer<U, OutboxStatus> outboxCallback,
                                                          String avroModelName) {
    return throwable -> {
      log.error(
          "Error while sending {} with message: {} and outbox type: {} to topic {}",
          avroModelName,
          avroModel.toString(),
          outboxMessage.getClass().getSimpleName(),
          topicName,
          throwable
      );
      outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
      return null;
    };
  }

  public <T, U> Consumer<SendResult<String, T>> successCallback(String orderId,
                                                                U outboxMessage,
                                                                BiConsumer<U, OutboxStatus> outboxCallback) {
    return result -> {
      RecordMetadata recordMetadata = result.getRecordMetadata();
      log.info(
          "Received successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
          orderId,
          recordMetadata.topic(),
          recordMetadata.partition(),
          recordMetadata.offset(),
          recordMetadata.timestamp()
      );
      outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
    };
  }

  public <T> T getOrderEventPayload(String payload, Class<T> outputType) {
    try {
      return objectMapper.readValue(payload, outputType);
    } catch (JsonProcessingException e) {
      throw new OrderDomainException("Could not read " + outputType.getSimpleName() + " object!", e);
    }
  }
}
