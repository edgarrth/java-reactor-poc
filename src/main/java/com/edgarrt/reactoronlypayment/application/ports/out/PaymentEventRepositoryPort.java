package com.edgarrt.reactoronlypayment.application.ports.out;
import com.edgarrt.reactoronlypayment.domain.model.PaymentEvent;
import reactor.core.publisher.Mono;
public interface PaymentEventRepositoryPort { Mono<PaymentEvent> save(PaymentEvent event); }
