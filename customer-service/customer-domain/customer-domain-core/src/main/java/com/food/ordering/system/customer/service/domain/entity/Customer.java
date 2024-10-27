package com.food.ordering.system.customer.service.domain.entity;

import id.rivasyafri.learning.domain.entity.AggregateRoot;
import id.rivasyafri.learning.domain.value.objects.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
  private final String username;
  private final String firstName;
  private final String lastName;

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

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}


