package it.bank.bankcore.user.domain.exception;

import it.bank.bankcore.shared.exception.ErrorCode;
import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;

public class UserAlreadyExistsException extends BusinessRuleConstraintException {
    public UserAlreadyExistsException(String fiscalCode, String email) {
        super(ErrorCode.USER_ALREADY_EXISTS, "User already exists for fiscal code: " + fiscalCode + " or email: " + email);
    }
}
