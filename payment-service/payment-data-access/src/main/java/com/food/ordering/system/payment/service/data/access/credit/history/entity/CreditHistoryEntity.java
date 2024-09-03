package com.food.ordering.system.payment.service.data.access.credit.history.entity;

import com.food.ordering.system.payment.service.domain.value.object.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credit_history")
@Entity
public class CreditHistoryEntity {
  @Id
  private UUID id;
  private UUID customerId;
  private BigDecimal amount;
  @Enumerated(EnumType.STRING)
  private TransactionType type;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreditHistoryEntity that = (CreditHistoryEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
