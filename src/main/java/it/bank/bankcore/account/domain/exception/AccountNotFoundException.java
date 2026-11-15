package it.bank.bankcore.account.domain.exception;

import it.bank.bankcore.shared.exception.ErrorCode;
import it.bank.bankcore.shared.exception.NotFoundException;

public class AccountNotFoundException extends NotFoundException {
    /**
     * Constructor for AccountNotFoundException.
     * @param uuid is the account uuid
     */
    public AccountNotFoundException(String uuid) {
        super(ErrorCode.ACCOUNT_NOT_FOUND, "Account not found for UUID: " + uuid);
    }
}
