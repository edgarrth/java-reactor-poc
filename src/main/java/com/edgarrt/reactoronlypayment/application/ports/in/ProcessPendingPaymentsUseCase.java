package com.edgarrt.reactoronlypayment.application.ports.in;
import com.edgarrt.reactoronlypayment.domain.model.Payment;
import reactor.core.publisher.Flux;
public interface ProcessPendingPaymentsUseCase { Flux<Payment> processPendingPayments(); }
