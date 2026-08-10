package com.edgarrt.reactoronlypayment.application.usecase;

import com.edgarrt.reactoronlypayment.application.ports.in.ProcessPendingPaymentsUseCase;
import com.edgarrt.reactoronlypayment.application.ports.out.PaymentEventRepositoryPort;
import com.edgarrt.reactoronlypayment.application.ports.out.PaymentRepositoryPort;
import com.edgarrt.reactoronlypayment.domain.model.Payment;
import com.edgarrt.reactoronlypayment.domain.model.PaymentEvent;
import com.edgarrt.reactoronlypayment.domain.service.PaymentAuthorizationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class PaymentProcessorService implements ProcessPendingPaymentsUseCase {

  private static final Logger log = LoggerFactory.getLogger(PaymentProcessorService.class);

  private final PaymentRepositoryPort repository;
  private final PaymentEventRepositoryPort events;
  private final PaymentAuthorizationPolicy policy;
  private final int batchSize;
  private final int concurrency;
  private final int limitRate;
  private final int maxRetries;

  public PaymentProcessorService(
      PaymentRepositoryPort repository,
      PaymentEventRepositoryPort events,
      PaymentAuthorizationPolicy policy,
      @Value("${payment-processor.batch-size}") int batchSize,
      @Value("${payment-processor.concurrency}") int concurrency,
      @Value("${payment-processor.limit-rate}") int limitRate,
      @Value("${payment-processor.max-retries}") int maxRetries) {
    this.repository = repository;
    this.events = events;
    this.policy = policy;
    this.batchSize = batchSize;
    this.concurrency = concurrency;
    this.limitRate = limitRate;
    this.maxRetries = maxRetries;
  }

  @Override
  public Flux<Payment> processPendingPayments() {
    return repository.findPending(batchSize)
        .limitRate(limitRate)
        .flatMap(this::processOne, concurrency)
        .onBackpressureBuffer(
            batchSize * 2,
            payment -> log.warn("Backpressure buffer full while processing payment {}", payment.id()))
        .doOnSubscribe(subscription -> log.info("Starting Reactor-only payment batch"))
        .doOnComplete(() -> log.info("Batch completed"));
  }

  private Mono<Payment> processOne(Payment payment) {
    return policy.authorize(payment)
        .delayElement(Duration.ofMillis(150))
        .flatMap(repository::save)
        .flatMap(saved -> events.save(PaymentEvent.of(
                saved.id(),
                "PAYMENT_" + saved.status(),
                "{\"status\":\"" + saved.status() + "\"}"))
            .thenReturn(saved))
        .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(200))
            .filter(ex -> !(ex instanceof IllegalArgumentException)))
        .onErrorResume(ex -> {
          log.error("Payment {} failed after retries", payment.id(), ex);
          // The attempt must be reflected even when processing fails before a successful save.
          return repository.save(payment.markProcessing().fail(safeMessage(ex)));
        });
  }

  private String safeMessage(Throwable ex) {
    String message = ex.getMessage();
    if (message == null || message.isBlank()) {
      return ex.getClass().getSimpleName();
    }
    return message.length() <= 250 ? message : message.substring(0, 250);
  }
}
