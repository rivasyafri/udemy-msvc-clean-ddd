package com.food.ordering.system.order.service.domain.value.objects;

import java.util.Objects;
import java.util.UUID;

public record StreetAddress(UUID id, String street, String postalCode, String city) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StreetAddress that = (StreetAddress) o;
    return Objects.equals(street, that.street)
        && Objects.equals(postalCode, that.postalCode)
        && Objects.equals(city, that.city);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, postalCode, city);
  }
}
