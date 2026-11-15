package it.bank.bankcore.user.application.result;

import it.bank.bankcore.user.domain.enums.UserStatus;

public record UserResult(
        String uuid,
        String firstName,
        String lastName,
        String email,
        String fiscalCode,
        String phoneNumber,
        String city,
        String state,
        String country,
        UserStatus status,
        Long createdAt
) {
}
