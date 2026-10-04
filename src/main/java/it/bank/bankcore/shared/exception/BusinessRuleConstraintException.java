package it.bank.bankcore.shared.exception;

public class BusinessRuleConstraintException extends RuntimeException {
    public String getCode() {
        return null;
    }

    public BusinessRuleConstraintException(String message) {
        super(message);
    }
}
