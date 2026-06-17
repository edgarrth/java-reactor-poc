package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import org.springframework.data.r2dbc.repository.Query; import org.springframework.data.repository.reactive.ReactiveCrudRepository; import reactor.core.publisher.Flux; import java.util.UUID;
public interface SpringDataPaymentRepository extends ReactiveCrudRepository<PaymentRow, UUID> { @Query("SELECT * FROM payments WHERE status = 'PENDING' ORDER BY created_at LIMIT :limit") Flux<PaymentRow> findPending(int limit); }
