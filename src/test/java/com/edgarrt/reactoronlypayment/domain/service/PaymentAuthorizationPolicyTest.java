package com.edgarrt.reactoronlypayment.domain.service;

import com.edgarrt.reactoronlypayment.domain.model.Payment;
import com.edgarrt.reactoronlypayment.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class PaymentAuthorizationPolicyTest {

  private final PaymentAuthorizationPolicy policy = new PaymentAuthorizationPolicy();

  @Test
  void authorizesPaymentAtOrBelowThreshold() {
    Payment payment = payment("500.00");

    StepVerifier.create(policy.authorize(payment))
        .assertNext(result -> {
          assertEquals(PaymentStatus.AUTHORIZED, result.status());
          assertEquals(1, result.attempts());
          assertNull(result.failureReason());
        })
        .verifyComplete();
  }

  @Test
  void rejectsPaymentAboveThreshold() {
    Payment payment = payment("500.01");

    StepVerifier.create(policy.authorize(payment))
        .assertNext(result -> {
          assertEquals(PaymentStatus.REJECTED, result.status());
          assertEquals(1, result.attempts());
          assertEquals("Amount exceeds PoC risk threshold", result.failureReason());
        })
        .verifyComplete();
  }

  private Payment payment(String amount) {
    LocalDateTime now = LocalDateTime.now();
    return new Payment(
        UUID.randomUUID(),
        "merchant-test",
        "customer-test",
        new BigDecimal(amount),
        "PEN",
        PaymentStatus.PENDING,
        0,
        null,
        now,
        now);
  }
}
