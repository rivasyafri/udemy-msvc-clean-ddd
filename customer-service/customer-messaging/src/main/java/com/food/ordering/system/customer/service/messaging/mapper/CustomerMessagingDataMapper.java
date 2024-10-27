package com.food.ordering.system.customer.service.messaging.mapper;

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomerMessagingDataMapper {

  public CustomerAvroModel paymentResponseAvroModelToPaymentResponse(CustomerCreatedEvent
                                                                         customerCreatedEvent) {
    return CustomerAvroModel.newBuilder()
        .setId(customerCreatedEvent.value().getId().getValue().toString())
        .setUsername(customerCreatedEvent.value().getUsername())
        .setFirstName(customerCreatedEvent.value().getFirstName())
        .setLastName(customerCreatedEvent.value().getLastName())
        .build();
  }
}
