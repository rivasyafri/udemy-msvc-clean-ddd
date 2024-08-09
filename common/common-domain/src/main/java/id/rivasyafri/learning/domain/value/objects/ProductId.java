package id.rivasyafri.learning.domain.value.objects;

import java.util.UUID;

public class ProductId extends BaseId<UUID> {
  protected ProductId(UUID value) {
    super(value);
  }
}
