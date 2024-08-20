package id.rivasyafri.learning.domain.value.objects;

import java.util.UUID;

public class OrderId extends BaseId<UUID> {
  public OrderId(UUID value) {
    super(value);
  }
}