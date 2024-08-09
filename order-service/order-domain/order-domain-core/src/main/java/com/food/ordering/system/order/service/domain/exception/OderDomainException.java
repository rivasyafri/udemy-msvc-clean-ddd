package com.food.ordering.system.order.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class OderDomainException extends DomainException {

  public OderDomainException(String message) {
    super(message);
  }

  public OderDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
