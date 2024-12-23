package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderOutboxScheduler implements OutboxScheduler {
  private final OrderOutboxHelper orderOutboxHelper;
  private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

  public OrderOutboxScheduler(OrderOutboxHelper orderOutboxHelper,
                              PaymentResponseMessagePublisher paymentResponseMessagePublisher) {
    this.orderOutboxHelper = orderOutboxHelper;
    this.paymentResponseMessagePublisher = paymentResponseMessagePublisher;
  }

  @Override
  @Transactional
  @Scheduled(
      fixedDelayString = "${payment-service.outbox-scheduler-fixed-rate}",
      initialDelayString = "${payment-service.outbox-scheduler-initial-delay}"
  )
  public void processOutboxMessage() {
    Optional<List<OrderOutboxMessage>> outboxMessageResponse =
        orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);
    if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
      List<OrderOutboxMessage> outboxMessages = outboxMessageResponse.get();
      log.info(
          "Received {} OrderOutboxMessage with ids: {}, sending to message bus!",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(OrderOutboxMessage::getId)
              .map(UUID::toString)
              .collect(Collectors.joining(","))
      );
      outboxMessages.forEach(outboxMessage -> paymentResponseMessagePublisher.publish(
          outboxMessage,
          orderOutboxHelper::updateOutboxStatus
      ));
      log.info("{} OrderOutboxMessage sent to message bus!", outboxMessages.size());
    }
  }
}
