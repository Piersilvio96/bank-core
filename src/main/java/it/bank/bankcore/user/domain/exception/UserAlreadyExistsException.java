package it.bank.bankcore.user.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class UserAlreadyExistsException extends BusinessRuleConstraintException {
    public UserAlreadyExistsException(String fiscalCode, String email) {
        super("User already exists for fiscal code: " + fiscalCode + " or email: " + email);
    }
}
