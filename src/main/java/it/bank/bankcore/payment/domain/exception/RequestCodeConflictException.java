package it.bank.bankcore.payment.domain.exception;

import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;
import it.bank.bankcore.shared.exception.ErrorCode;

public class RequestCodeConflictException extends BusinessRuleConstraintException {
    public RequestCodeConflictException() {
        super(ErrorCode.REQUEST_CODE_CONFLICT, "Request code is already associated with different payment data");
    }
}
