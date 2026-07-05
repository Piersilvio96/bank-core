package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class AccountAlreadyExistsException extends BusinessRuleConstraintException {

    public AccountAlreadyExistsException(String fiscalCode, String email) {
        super("Account already exists for fiscal code: " + fiscalCode + " or email: " + email);
    }
}

