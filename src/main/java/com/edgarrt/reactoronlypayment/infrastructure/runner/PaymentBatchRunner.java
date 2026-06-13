package com.edgarrt.reactoronlypayment.infrastructure.runner;
import com.edgarrt.reactoronlypayment.application.ports.in.ProcessPendingPaymentsUseCase;
import org.springframework.beans.factory.annotation.Value; import org.springframework.boot.CommandLineRunner; import org.springframework.stereotype.Component;
import reactor.core.Disposable; import reactor.core.publisher.Flux;
import java.time.Duration;
@Component public class PaymentBatchRunner implements CommandLineRunner {
  private final ProcessPendingPaymentsUseCase useCase; private final boolean autoRun; private final long pollSeconds;
  public PaymentBatchRunner(ProcessPendingPaymentsUseCase useCase, @Value("${payment-processor.auto-run-on-startup}") boolean autoRun, @Value("${payment-processor.poll-interval-seconds}") long pollSeconds){ this.useCase = useCase; this.autoRun = autoRun; this.pollSeconds = pollSeconds; }
  @Override public void run(String... args) {
    if (!autoRun) return;
    Disposable ignored = Flux.interval(Duration.ZERO, Duration.ofSeconds(pollSeconds))
      .concatMap(tick -> useCase.processPendingPayments().collectList())
      .doOnNext(list -> System.out.println("Processed payments in tick: " + list.size()))
      .subscribe();
  }
}
