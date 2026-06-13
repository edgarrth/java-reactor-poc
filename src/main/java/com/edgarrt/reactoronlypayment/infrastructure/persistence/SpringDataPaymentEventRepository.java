package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import org.springframework.data.repository.reactive.ReactiveCrudRepository; import java.util.UUID;
public interface SpringDataPaymentEventRepository extends ReactiveCrudRepository<PaymentEventRow, UUID> {}
