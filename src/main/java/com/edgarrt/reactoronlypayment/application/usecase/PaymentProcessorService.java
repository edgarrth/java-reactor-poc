package com.edgarrt.reactoronlypayment.application.usecase;
import com.edgarrt.reactoronlypayment.application.ports.in.ProcessPendingPaymentsUseCase;
import com.edgarrt.reactoronlypayment.application.ports.out.PaymentEventRepositoryPort;
import com.edgarrt.reactoronlypayment.application.ports.out.PaymentRepositoryPort;
import com.edgarrt.reactoronlypayment.domain.model.*;
import com.edgarrt.reactoronlypayment.domain.service.PaymentAuthorizationPolicy;
import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux; import reactor.core.publisher.Mono; import reactor.util.retry.Retry;
import java.time.Duration;
@Service
public class PaymentProcessorService implements ProcessPendingPaymentsUseCase {
  private final PaymentRepositoryPort repository; private final PaymentEventRepositoryPort events; private final PaymentAuthorizationPolicy policy = new PaymentAuthorizationPolicy();
  private final int batchSize; private final int concurrency; private final int limitRate; private final int maxRetries;
  public PaymentProcessorService(PaymentRepositoryPort repository, PaymentEventRepositoryPort events,
    @Value("${payment-processor.batch-size}") int batchSize, @Value("${payment-processor.concurrency}") int concurrency,
    @Value("${payment-processor.limit-rate}") int limitRate, @Value("${payment-processor.max-retries}") int maxRetries) {
    this.repository = repository; this.events = events; this.batchSize = batchSize; this.concurrency = concurrency; this.limitRate = limitRate; this.maxRetries = maxRetries;
  }
  @Override public Flux<Payment> processPendingPayments() {
    return repository.findPending(batchSize)
      .limitRate(limitRate)
      .flatMap(this::processOne, concurrency)
      .onBackpressureBuffer(batchSize * 2, p -> System.out.println("Backpressure buffer: " + p.id()))
      .doOnSubscribe(s -> System.out.println("Starting Reactor-only payment batch"))
      .doOnComplete(() -> System.out.println("Batch completed"));
  }
  private Mono<Payment> processOne(Payment payment) {
    return policy.authorize(payment)
      .delayElement(Duration.ofMillis(150))
      .flatMap(repository::save)
      .flatMap(saved -> events.save(PaymentEvent.of(saved.id(), "PAYMENT_" + saved.status(), "{\"status\":\"" + saved.status() + "\"}" )).thenReturn(saved))
      .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(200)).filter(ex -> !(ex instanceof IllegalArgumentException)))
      .onErrorResume(ex -> repository.save(payment.fail(ex.getMessage())));
  }
}
