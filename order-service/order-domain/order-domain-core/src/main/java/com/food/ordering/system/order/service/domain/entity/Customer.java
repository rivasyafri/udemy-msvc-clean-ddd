package com.food.ordering.system.order.service.domain.entity;

import id.rivasyafri.learning.domain.entity.AggregateRoot;
import id.rivasyafri.learning.domain.value.objects.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

  private String username;
  private String firstName;
  private String lastName;

  public Customer(CustomerId customerId) {
    super.setId(customerId);
  }

  public Customer(CustomerId customerId,
                  String username,
                  String firstName,
                  String lastName) {
    super.setId(customerId);
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getUsername() {
    return username;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
}
