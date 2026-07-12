package it.bank.bankcore.payment.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class PaymentStatusInvalid extends BusinessRuleConstraintException {
    public PaymentStatusInvalid(String message) {
        super(message);
    }
}
