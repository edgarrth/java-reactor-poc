package com.edgarrt.reactoronlypayment.infrastructure.persistence;

import com.edgarrt.reactoronlypayment.application.ports.out.PaymentEventRepositoryPort;
import com.edgarrt.reactoronlypayment.domain.model.PaymentEvent;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Append-only persistence adapter for payment events.
 *
 * <p>Payment events are created with their UUID in the domain. Using
 * ReactiveCrudRepository.save(...) would classify a row with a non-null @Id as
 * an existing entity and issue an UPDATE. Events are immutable/append-only, so
 * this adapter deliberately uses R2dbcEntityTemplate.insert(...) to force an
 * INSERT for every new domain event.</p>
 */
@Component
public class R2dbcPaymentEventAdapter implements PaymentEventRepositoryPort {

  private final R2dbcEntityTemplate template;

  public R2dbcPaymentEventAdapter(R2dbcEntityTemplate template) {
    this.template = template;
  }

  @Override
  public Mono<PaymentEvent> save(PaymentEvent event) {
    PaymentEventRow row = PaymentMapper.toRow(event);
    return template.insert(row).map(PaymentMapper::toDomain);
  }
}
