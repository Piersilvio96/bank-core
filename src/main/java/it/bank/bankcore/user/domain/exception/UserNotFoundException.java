package it.bank.bankcore.user.domain.exception;

import it.bank.bankcore.shared.exception.ErrorCode;
import it.bank.bankcore.shared.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String uuid) {
        super(ErrorCode.USER_NOT_FOUND, "User not found for UUID: " + uuid);
    }
}
