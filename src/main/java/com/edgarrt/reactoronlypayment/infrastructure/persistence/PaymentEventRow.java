package com.edgarrt.reactoronlypayment.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("payment_events")
public record PaymentEventRow(
    @Id UUID id,
    @Column("payment_id") UUID paymentId,
    @Column("event_type") String eventType,
    String payload,
    @Column("created_at") LocalDateTime createdAt) {
}
