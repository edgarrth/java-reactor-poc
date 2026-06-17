package com.edgarrt.reactoronlypayment.domain.model;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.UUID;
public record Payment(UUID id, String merchantId, String customerId, BigDecimal amount, String currency, PaymentStatus status, int attempts, String failureReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
  public Payment markProcessing(){ return new Payment(id, merchantId, customerId, amount, currency, PaymentStatus.PROCESSING, attempts+1, null, createdAt, LocalDateTime.now()); }
  public Payment authorize(){ return new Payment(id, merchantId, customerId, amount, currency, PaymentStatus.AUTHORIZED, attempts, null, createdAt, LocalDateTime.now()); }
  public Payment reject(String reason){ return new Payment(id, merchantId, customerId, amount, currency, PaymentStatus.REJECTED, attempts, reason, createdAt, LocalDateTime.now()); }
  public Payment fail(String reason){ return new Payment(id, merchantId, customerId, amount, currency, PaymentStatus.FAILED, attempts, reason, createdAt, LocalDateTime.now()); }
}
