package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import org.springframework.data.annotation.Id; import org.springframework.data.relational.core.mapping.Table; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.UUID;
@Table("payments") public record PaymentRow(@Id UUID id, String merchantId, String customerId, BigDecimal amount, String currency, String status, int attempts, String failureReason, LocalDateTime createdAt, LocalDateTime updatedAt) {}
