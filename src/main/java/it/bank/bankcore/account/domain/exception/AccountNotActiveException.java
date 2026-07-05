package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class AccountNotActiveException extends BusinessRuleConstraintException {
    public AccountNotActiveException(String message) {
        super(message);
    }
}
