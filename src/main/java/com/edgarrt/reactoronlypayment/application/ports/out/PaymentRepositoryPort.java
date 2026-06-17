package com.edgarrt.reactoronlypayment.application.ports.out;
import com.edgarrt.reactoronlypayment.domain.model.Payment;
import reactor.core.publisher.Flux; import reactor.core.publisher.Mono;
public interface PaymentRepositoryPort { Flux<Payment> findPending(int limit); Mono<Payment> save(Payment payment); }
