package it.bank.bankcore.payment.domain.repository;

import it.bank.bankcore.payment.domain.model.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);
}

