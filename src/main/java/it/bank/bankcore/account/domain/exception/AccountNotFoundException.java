package it.bank.bankcore.account.domain.exception;

import jakarta.persistence.EntityNotFoundException;

public class AccountNotFoundException extends EntityNotFoundException {
    public AccountNotFoundException() {
        super("Account not found");
    }
}
