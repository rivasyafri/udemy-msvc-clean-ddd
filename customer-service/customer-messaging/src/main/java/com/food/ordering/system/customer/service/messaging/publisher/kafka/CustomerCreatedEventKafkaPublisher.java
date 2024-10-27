package com.food.ordering.system.customer.service.messaging.publisher.kafka;

import com.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerCreatedEventKafkaPublisher implements CustomerMessagePublisher {

  private final CustomerMessagingDataMapper customerMessagingDataMapper;

  private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;

  private final CustomerServiceConfigData customerServiceConfigData;

  private final KafkaMessageHelper kafkaMessageHelper;

  public CustomerCreatedEventKafkaPublisher(CustomerMessagingDataMapper customerMessagingDataMapper,
                                            KafkaProducer<String, CustomerAvroModel> kafkaProducer,
                                            CustomerServiceConfigData customerServiceConfigData,
                                            KafkaMessageHelper kafkaMessageHelper) {
    this.customerMessagingDataMapper = customerMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.customerServiceConfigData = customerServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(CustomerCreatedEvent customerCreatedEvent) {
    log.info(
        "Received CustomerCreatedEvent for customer id: {}",
        customerCreatedEvent.value().getId().getValue()
    );
    try {
      CustomerAvroModel customerAvroModel = customerMessagingDataMapper
          .paymentResponseAvroModelToPaymentResponse(customerCreatedEvent);

      kafkaProducer.send(
          customerServiceConfigData.getCustomerTopicName(),
          customerAvroModel.getId(),
          customerAvroModel,
          kafkaMessageHelper.successCallback(
              customerAvroModel.getId(),
              customerAvroModel,
              null
          ),
          kafkaMessageHelper.failureCallback(
              customerServiceConfigData.getCustomerTopicName(),
              customerAvroModel,
              customerAvroModel,
              null,
              CustomerAvroModel.class.getSimpleName()
          )
      );

      log.info(
          "CustomerCreatedEvent sent to kafka for customer id: {}",
          customerAvroModel.getId()
      );
    } catch (Exception e) {
      log.error("Error while sending CustomerCreatedEvent to kafka for customer id: {}," +
                    " error: {}", customerCreatedEvent.value().getId().getValue(), e.getMessage());
    }
  }
}
