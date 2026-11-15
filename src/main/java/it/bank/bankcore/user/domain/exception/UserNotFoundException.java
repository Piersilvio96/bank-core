package it.bank.bankcore.user.domain.exception;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String uuid) {
        super("User not found for UUID: " + uuid);
    }
}
