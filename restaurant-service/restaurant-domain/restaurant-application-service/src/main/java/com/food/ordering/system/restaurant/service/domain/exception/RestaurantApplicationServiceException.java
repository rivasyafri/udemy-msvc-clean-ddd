package com.food.ordering.system.restaurant.service.domain.exception;

import id.rivasyafri.learning.domain.exception.DomainException;

public class RestaurantApplicationServiceException extends DomainException {
  public RestaurantApplicationServiceException(String message) {
    super(message);
  }

  public RestaurantApplicationServiceException(String message,
                                               Throwable cause) {
    super(message, cause);
  }
}
