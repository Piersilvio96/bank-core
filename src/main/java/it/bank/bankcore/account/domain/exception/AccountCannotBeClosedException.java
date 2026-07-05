package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class AccountCannotBeClosedException extends BusinessRuleConstraintException {

    public AccountCannotBeClosedException(String message) {
        super(message);
    }
}
