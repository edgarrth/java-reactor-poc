package com.edgarrt.reactoronlypayment.infrastructure.persistence;

import com.edgarrt.reactoronlypayment.domain.model.PaymentEvent;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class R2dbcPaymentEventAdapterTest {

  @Test
  void insertsNewEventsInsteadOfTryingToUpdateThem() {
    R2dbcEntityTemplate template = mock(R2dbcEntityTemplate.class);
    R2dbcPaymentEventAdapter adapter = new R2dbcPaymentEventAdapter(template);
    PaymentEvent event = PaymentEvent.of(UUID.randomUUID(), "PAYMENT_AUTHORIZED", "{\"status\":\"AUTHORIZED\"}");

    when(template.insert(any(PaymentEventRow.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, PaymentEventRow.class)));

    StepVerifier.create(adapter.save(event))
        .expectNext(event)
        .verifyComplete();

    verify(template).insert(any(PaymentEventRow.class));
  }
}
