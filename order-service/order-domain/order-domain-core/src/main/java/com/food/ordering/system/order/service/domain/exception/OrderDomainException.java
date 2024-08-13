package com.food.ordering.system.order.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class OrderDomainException extends DomainException {

  public OrderDomainException(String message) {
    super(message);
  }

  public OrderDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
