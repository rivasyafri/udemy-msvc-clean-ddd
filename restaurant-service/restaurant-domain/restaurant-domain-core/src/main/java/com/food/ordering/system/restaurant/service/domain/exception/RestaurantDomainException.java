package com.food.ordering.system.restaurant.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class RestaurantDomainException extends DomainException {
  public RestaurantDomainException(String message) {
    super(message);
  }

  public RestaurantDomainException(String message,
                                   Throwable cause) {
    super(message, cause);
  }
}
