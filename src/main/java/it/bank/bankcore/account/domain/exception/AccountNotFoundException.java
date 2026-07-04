package it.bank.bankcore.account.domain.exception;

import jakarta.persistence.EntityNotFoundException;

public class AccountNotFoundException extends EntityNotFoundException {
    /**
     * Constructor for AccountNotFoundException.
     * @param uuid is the account uuid
     */
    public AccountNotFoundException(String uuid) {
        super("Account not found for UUID: " + uuid);
    }
}
