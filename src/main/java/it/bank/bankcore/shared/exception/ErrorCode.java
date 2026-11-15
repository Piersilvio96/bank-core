package it.bank.bankcore.shared.exception;

/**
 * Stable, machine-readable error codes returned in {@code ErrorResponse.code}.
 * Clients must branch on these codes, never on the human-readable message.
 * Renaming or removing a value is a breaking API change.
 */
public enum ErrorCode {
    // Request shape
    VALIDATION_FAILED,
    INVALID_REQUEST,

    // Not found
    USER_NOT_FOUND,
    ACCOUNT_NOT_FOUND,
    PAYMENT_NOT_FOUND,

    // Users
    USER_ALREADY_EXISTS,

    // Accounts
    ACCOUNT_NOT_ACTIVE,
    ACCOUNT_NOT_CLOSABLE,
    INSUFFICIENT_FUNDS,
    INVALID_AMOUNT,

    // Payments
    SAME_ACCOUNT_TRANSFER,
    CURRENCY_MISMATCH,
    REQUEST_CODE_CONFLICT,
    PAYMENT_NOT_REVERSIBLE,

    // Fallback
    INTERNAL_ERROR
}
