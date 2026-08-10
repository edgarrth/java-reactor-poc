package com.edgarrt.reactoronlypayment.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("payments")
public record PaymentRow(
    @Id UUID id,
    @Column("merchant_id") String merchantId,
    @Column("customer_id") String customerId,
    BigDecimal amount,
    String currency,
    String status,
    int attempts,
    @Column("failure_reason") String failureReason,
    @Column("created_at") LocalDateTime createdAt,
    @Column("updated_at") LocalDateTime updatedAt) {
}
