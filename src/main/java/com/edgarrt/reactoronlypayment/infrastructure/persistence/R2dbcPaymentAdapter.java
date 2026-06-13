package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import com.edgarrt.reactoronlypayment.application.ports.out.PaymentRepositoryPort; import com.edgarrt.reactoronlypayment.domain.model.Payment;
import org.springframework.stereotype.Component; import reactor.core.publisher.Flux; import reactor.core.publisher.Mono;
@Component public class R2dbcPaymentAdapter implements PaymentRepositoryPort {
  private final SpringDataPaymentRepository repository; public R2dbcPaymentAdapter(SpringDataPaymentRepository repository){ this.repository = repository; }
  public Flux<Payment> findPending(int limit){ return repository.findPending(limit).map(PaymentMapper::toDomain); }
  public Mono<Payment> save(Payment payment){ return repository.save(PaymentMapper.toRow(payment)).map(PaymentMapper::toDomain); }
}
