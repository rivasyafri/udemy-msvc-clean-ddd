package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.system.order.service.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.saga.SagaStatus;
import id.rivasyafri.learning.domain.value.objects.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.food.ordering.system.saga.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
@Sql(value = {"classpath:sql/OrderPaymentSagaTestSetUp.sql"})
@Sql(value = {"classpath:sql/OrderPaymentSagaTestCleanUp.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderPaymentSagaTest {
  private static final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
  private static final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb17");
  private static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
  private static final UUID PAYMENT_ID = UUID.randomUUID();
  private static final BigDecimal PRICE = new BigDecimal(100);
  @Autowired
  private OrderPaymentSaga orderPaymentSaga;
  @Autowired
  private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

  @Test
  void testDoublePayment() {
    orderPaymentSaga.process(getPaymentResponse());
    orderPaymentSaga.process(getPaymentResponse());
  }

  @Test
  void testDoublePaymentWithThreads() {
    try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
      List<Callable<Object>> task = new ArrayList<>();

      task.add(Executors.callable(() -> {
        try {
          orderPaymentSaga.process(getPaymentResponse());
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
          orderPaymentSaga.process(getPaymentResponse());
        } catch (DataAccessException e) {
          log.error(
              "{} occurred for thread 2 with sql state: {}",
              e.getClass().getSimpleName(),
              ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState()
          );
        }
      }));
      executorService.invokeAll(task);
      assertPaymentOutbox();
    } catch (InterruptedException e) {
      log.error("Error calling this test method due to InterruptedException", e);
    }
  }

  private PaymentResponse getPaymentResponse() {
    return PaymentResponse.builder()
        .id(UUID.randomUUID())
        .sagaId(SAGA_ID)
        .paymentStatus(PaymentStatus.COMPLETED)
        .paymentId(PAYMENT_ID)
        .orderId(ORDER_ID)
        .customerId(CUSTOMER_ID)
        .price(PRICE)
        .createdAt(Instant.now())
        .failureMessages(new ArrayList<>())
        .build();
  }

  private void assertPaymentOutbox() {
    Optional<PaymentOutboxEntity> paymentOutboxEntity =
        paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, SAGA_ID,
                                                                      List.of(SagaStatus.PROCESSING)
        );
    assertTrue(paymentOutboxEntity.isPresent());
  }
}
