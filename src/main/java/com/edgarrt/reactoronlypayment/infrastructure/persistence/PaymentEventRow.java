package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import org.springframework.data.annotation.Id; import org.springframework.data.relational.core.mapping.Table; import java.time.LocalDateTime; import java.util.UUID;
@Table("payment_events") public record PaymentEventRow(@Id UUID id, UUID paymentId, String eventType, String payload, LocalDateTime createdAt) {}
