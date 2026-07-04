package it.bank.bankcore.shared.exception;

public class BusinessRuleInputException extends RuntimeException {
    public BusinessRuleInputException(String message) {
        super(message);
    }
}
