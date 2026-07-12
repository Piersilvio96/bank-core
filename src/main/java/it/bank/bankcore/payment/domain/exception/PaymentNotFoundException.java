package it.bank.bankcore.payment.domain.exception;

import jakarta.persistence.EntityNotFoundException;

public class PaymentNotFoundException extends EntityNotFoundException {

    private final String paymentId;

    public PaymentNotFoundException(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String getMessage() {
        return "Payment with ID " + paymentId + " not found.";
    }
}
