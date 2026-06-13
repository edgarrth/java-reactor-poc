package com.edgarrt.reactoronlypayment.infrastructure.persistence;
import com.edgarrt.reactoronlypayment.domain.model.*;
final class PaymentMapper {
  static Payment toDomain(PaymentRow r){ return new Payment(r.id(), r.merchantId(), r.customerId(), r.amount(), r.currency(), PaymentStatus.valueOf(r.status()), r.attempts(), r.failureReason(), r.createdAt(), r.updatedAt()); }
  static PaymentRow toRow(Payment p){ return new PaymentRow(p.id(), p.merchantId(), p.customerId(), p.amount(), p.currency(), p.status().name(), p.attempts(), p.failureReason(), p.createdAt(), p.updatedAt()); }
  static PaymentEventRow toRow(PaymentEvent e){ return new PaymentEventRow(e.id(), e.paymentId(), e.eventType(), e.payload(), e.createdAt()); }
  static PaymentEvent toDomain(PaymentEventRow r){ return new PaymentEvent(r.id(), r.paymentId(), r.eventType(), r.payload(), r.createdAt()); }
}
