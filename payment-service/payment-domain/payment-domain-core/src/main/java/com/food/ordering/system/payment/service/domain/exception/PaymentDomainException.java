package com.food.ordering.system.payment.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class PaymentDomainException extends DomainException {
  public PaymentDomainException(String message) {
    super(message);
  }

  public PaymentDomainException(String message,
                                Throwable cause) {
    super(message, cause);
  }
}
