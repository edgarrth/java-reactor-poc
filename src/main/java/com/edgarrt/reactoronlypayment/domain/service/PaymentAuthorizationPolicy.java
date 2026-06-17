package com.edgarrt.reactoronlypayment.domain.service;
import com.edgarrt.reactoronlypayment.domain.model.Payment;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
public class PaymentAuthorizationPolicy {
  public Mono<Payment> authorize(Payment payment) {
    return Mono.just(payment)
      .map(Payment::markProcessing)
      .flatMap(p -> p.amount().compareTo(new BigDecimal("500.00")) > 0
        ? Mono.just(p.reject("Amount exceeds PoC risk threshold"))
        : Mono.just(p.authorize()));
  }
}
