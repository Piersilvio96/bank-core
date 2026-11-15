package it.bank.bankcore.shared.exception;

import lombok.Getter;

/**
 * The request is well-formed but breaks a business rule (HTTP 400).
 */
@Getter
public class BusinessRuleInputException extends RuntimeException {

    private final ErrorCode code;

    public BusinessRuleInputException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
