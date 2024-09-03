package com.food.ordering.system.payment.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class PaymentNotFoundException extends DomainException {
  public PaymentNotFoundException(String message) {
    super(message);
  }

  public PaymentNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
