package it.bank.bankcore.shared.exception;

public class BusinessRuleConstraintException extends RuntimeException {
    public BusinessRuleConstraintException(String message) {
        super(message);
    }
}
