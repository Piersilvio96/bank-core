package it.bank.bankcore.shared.exception;

import lombok.Getter;

/**
 * A business rule conflicts with the current state of the system (HTTP 409).
 */
@Getter
public class BusinessRuleConstraintException extends RuntimeException {

    private final ErrorCode code;

    public BusinessRuleConstraintException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
