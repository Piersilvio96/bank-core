package it.bank.bankcore.payment.domain.repository;

import it.bank.bankcore.payment.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment updateReversedPayment(Payment payment);

    Optional<Payment> findByRequestCode(String requestCode);

    Optional<Payment> findByPaymentId(String paymentId);
}

