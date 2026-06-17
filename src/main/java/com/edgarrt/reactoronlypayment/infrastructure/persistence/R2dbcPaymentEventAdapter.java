package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import com.edgarrt.reactoronlypayment.application.ports.out.PaymentEventRepositoryPort; import com.edgarrt.reactoronlypayment.domain.model.PaymentEvent;
import org.springframework.stereotype.Component; import reactor.core.publisher.Mono;
@Component public class R2dbcPaymentEventAdapter implements PaymentEventRepositoryPort {
  private final SpringDataPaymentEventRepository repository; public R2dbcPaymentEventAdapter(SpringDataPaymentEventRepository repository){ this.repository = repository; }
  public Mono<PaymentEvent> save(PaymentEvent event){ return repository.save(PaymentMapper.toRow(event)).map(PaymentMapper::toDomain); }
}
