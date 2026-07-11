package it.bank.bankcore.payment.infrastructure.exception;

import it.bank.bankcore.shared.exception.InfrastructureException;

public class PaymentCodeAlreadyExists extends InfrastructureException {
    public PaymentCodeAlreadyExists(String message) {
        super(message);
    }
}
