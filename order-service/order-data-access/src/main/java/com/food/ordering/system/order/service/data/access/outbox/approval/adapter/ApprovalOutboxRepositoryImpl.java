package com.food.ordering.system.order.service.data.access.outbox.approval.adapter;

import com.food.ordering.system.order.service.data.access.outbox.approval.mapper.ApprovalOutboxDataAccessMapper;
import com.food.ordering.system.order.service.data.access.outbox.approval.repository.ApprovalOutboxJpaRepository;
import com.food.ordering.system.order.service.data.access.outbox.payment.exception.PaymentOutboxNotFoundException;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {
  private final ApprovalOutboxJpaRepository approvalOutboxJpaRepository;
  private final ApprovalOutboxDataAccessMapper approvalOutboxDataAccessMapper;

  public ApprovalOutboxRepositoryImpl(ApprovalOutboxJpaRepository approvalOutboxJpaRepository,
                                      ApprovalOutboxDataAccessMapper approvalOutboxDataAccessMapper) {
    this.approvalOutboxJpaRepository = approvalOutboxJpaRepository;
    this.approvalOutboxDataAccessMapper = approvalOutboxDataAccessMapper;
  }

  @Override
  public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderPaymentOutboxMessage) {
    return approvalOutboxDataAccessMapper
        .approvalOutboxEntityToOrderApprovalOutboxMessage(
            approvalOutboxJpaRepository.save(
                approvalOutboxDataAccessMapper
                    .orderApprovalOutboxMessageToOutboxEntity(orderPaymentOutboxMessage)
            )
        );
  }

  @Override
  public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                           OutboxStatus outboxStatus,
                                                                                           SagaStatus... sagaStatus) {
    return Optional.of(
        approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                                                                             Arrays.asList(sagaStatus)
            )
            .orElseThrow(() -> new PaymentOutboxNotFoundException("Payment outbox object " +
                                                                      "could not be found for saga type " + type))
            .stream()
            .map(approvalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
            .toList()
    );
  }

  @Override
  public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                               UUID sagaId,
                                                                               SagaStatus... sagaStatus) {
    return approvalOutboxJpaRepository
        .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
        .map(approvalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage);
  }

  @Override
  public void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                       OutboxStatus outboxStatus,
                                                       SagaStatus... sagaStatus) {
    approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                                                                           Arrays.asList(sagaStatus)
    );
  }
}
