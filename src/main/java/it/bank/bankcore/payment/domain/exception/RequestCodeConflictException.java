package it.bank.bankcore.payment.domain.exception;
import it.bank.bankcore.shared.exception.BusinessRuleConstraintException;
public class RequestCodeConflictException extends BusinessRuleConstraintException {
    @Override
    public String getCode() {
        return "REQUEST_CODE_CONFLICT";
    }

    public RequestCodeConflictException() {
        super("Request code is already associated with different payment data");
    }
}
