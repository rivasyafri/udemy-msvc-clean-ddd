package com.food.ordering.system.restaurant.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {
  public RestaurantNotFoundException(String message) {
    super(message);
  }

  public RestaurantNotFoundException(String message,
                                     Throwable cause) {
    super(message, cause);
  }
}
