package com.edgarrt.reactoronlypayment.domain.model;
import java.time.LocalDateTime; import java.util.UUID;
public record PaymentEvent(UUID id, UUID paymentId, String eventType, String payload, LocalDateTime createdAt) {
  public static PaymentEvent of(UUID paymentId, String type, String payload){ return new PaymentEvent(UUID.randomUUID(), paymentId, type, payload, LocalDateTime.now()); }
}
