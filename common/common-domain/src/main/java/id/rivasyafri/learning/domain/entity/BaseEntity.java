package id.rivasyafri.learning.domain.entity;

import java.util.Objects;

public abstract class BaseEntity<I> {
  private I id;

  public I getId() {
    return id;
  }

  public void setId(I id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BaseEntity<?> that = (BaseEntity<?>) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
