package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class InvalidAmountException extends BusinessRuleConstraintException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
