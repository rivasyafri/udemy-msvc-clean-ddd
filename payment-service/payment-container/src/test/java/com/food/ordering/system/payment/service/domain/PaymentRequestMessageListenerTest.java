package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.data.access.outbox.entity.OrderOutboxEntity;
import com.food.ordering.system.payment.service.data.access.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import id.rivasyafri.learning.domain.value.objects.PaymentOrderStatus;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import id.rivasyafri.learning.domain.value.objects.UUIDv7;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.food.ordering.system.saga.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
class PaymentRequestMessageListenerTest {
  @Autowired
  private PaymentRequestMessageListener paymentRequestMessageListener;
  @Autowired
  private OrderOutboxJpaRepository orderOutboxJpaRepository;

  private static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
  private static final BigDecimal PRICE = new BigDecimal(100);

  @Test
  void testDoublePayment() {
    UUID sagaId = UUIDv7.randomUUID();
    paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
    try {
      paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
    } catch (DataAccessException e) {
      log.error(
          "{} occurred with sql state: {}",
          e.getClass().getSimpleName(),
          ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState()
      );
    }
    assertOrderOutbox(sagaId);
  }

  @Test
  void testDoublePaymentWithThreads() {
    UUID sagaId = UUIDv7.randomUUID();
    try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
      List<Callable<Object>> task = new ArrayList<>();

      task.add(Executors.callable(() -> {
        try {
          paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
        } catch (DataAccessException e) {
          log.error(
              "{} occurred for thread 1 with sql state: {}",
              e.getClass().getSimpleName(),
              ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState()
          );
        }
      }));
      task.add(Executors.callable(() -> {
        try {
          paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
        } catch (DataAccessException e) {
          log.error(
              "{} occurred for thread 2 with sql state: {}",
              e.getClass().getSimpleName(),
              ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState()
          );
        }
      }));
      executorService.invokeAll(task);
      assertOrderOutbox(sagaId);
    } catch (InterruptedException e) {
      log.error("Error calling this test method due to InterruptedException", e);
    }
  }

  private PaymentRequest getPaymentRequest(UUID sagaId) {
    return PaymentRequest.builder()
        .id(UUID.randomUUID())
        .sagaId(sagaId)
        .orderId(UUIDv7.randomUUID())
        .paymentOrderStatus(PaymentOrderStatus.PENDING)
        .customerId(CUSTOMER_ID)
        .price(PRICE)
        .createdAt(Instant.now())
        .build();
  }

  private void assertOrderOutbox(UUID sagaId) {
    Optional<OrderOutboxEntity> orderOutboxEntity =
        orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            ORDER_SAGA_NAME,
            sagaId,
            PaymentStatus.COMPLETED,
            OutboxStatus.STARTED);
    assertTrue(orderOutboxEntity.isPresent());
    assertEquals(sagaId, orderOutboxEntity.get().getSagaId());
  }
}
