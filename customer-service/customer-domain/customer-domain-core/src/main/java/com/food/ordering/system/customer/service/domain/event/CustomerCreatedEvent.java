package com.food.ordering.system.customer.service.domain.event;

import com.food.ordering.system.customer.service.domain.entity.Customer;
import id.rivasyafri.learning.domain.event.DomainEvent;

public class CustomerCreatedEvent implements DomainEvent<Customer> {

  private final Customer customer;

  public CustomerCreatedEvent(Customer customer) {
    this.customer = customer;
  }

  public Customer value() {
    return customer;
  }
}
