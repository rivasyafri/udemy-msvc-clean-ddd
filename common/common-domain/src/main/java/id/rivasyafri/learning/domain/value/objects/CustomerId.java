package id.rivasyafri.learning.domain.value.objects;

import java.util.UUID;

public class CustomerId extends BaseId<UUID> {
  protected CustomerId(UUID value) {
    super(value);
  }
}
