package com.edgarrt.reactoronlypayment.infrastructure.runner;

import com.edgarrt.reactoronlypayment.application.ports.in.ProcessPendingPaymentsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Component
public class PaymentBatchRunner implements CommandLineRunner, DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(PaymentBatchRunner.class);

  private final ProcessPendingPaymentsUseCase useCase;
  private final boolean autoRun;
  private final long pollSeconds;

  private Scheduler pollingScheduler;
  private Disposable subscription;

  public PaymentBatchRunner(
      ProcessPendingPaymentsUseCase useCase,
      @Value("${payment-processor.auto-run-on-startup}") boolean autoRun,
      @Value("${payment-processor.poll-interval-seconds}") long pollSeconds) {
    this.useCase = useCase;
    this.autoRun = autoRun;
    this.pollSeconds = pollSeconds;
  }

  @Override
  public void run(String... args) {
    if (!autoRun) {
      log.info("Automatic payment polling is disabled");
      return;
    }

    // A non-daemon scheduler keeps this non-web worker JVM alive until Spring shuts down.
    pollingScheduler = Schedulers.newSingle("payment-poller", false);

    subscription = Flux.interval(Duration.ZERO, Duration.ofSeconds(pollSeconds), pollingScheduler)
        .concatMap(tick -> useCase.processPendingPayments()
            .collectList()
            .onErrorResume(ex -> {
              // A transient database/source failure must not terminate all future polling ticks.
              log.error("Payment batch tick {} failed; polling will continue", tick, ex);
              return Mono.just(List.of());
            }))
        .doOnNext(list -> log.info("Processed payments in tick: {}", list.size()))
        .subscribe(
            ignored -> { },
            ex -> log.error("Payment polling terminated unexpectedly", ex));
  }

  @Override
  public void destroy() {
    if (subscription != null) {
      subscription.dispose();
    }
    if (pollingScheduler != null) {
      pollingScheduler.dispose();
    }
  }
}
