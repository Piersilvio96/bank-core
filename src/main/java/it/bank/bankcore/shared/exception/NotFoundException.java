package it.bank.bankcore.shared.exception;

import lombok.Getter;

/**
 * A requested resource does not exist (HTTP 404).
 * Deliberately not a JPA exception, so Spring does not translate it when thrown inside a repository.
 */
@Getter
public class NotFoundException extends RuntimeException {

    private final ErrorCode code;

    public NotFoundException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
