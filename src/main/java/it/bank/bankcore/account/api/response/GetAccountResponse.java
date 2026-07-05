package it.bank.bankcore.account.api.response;

import it.bank.bankcore.account.domain.enums.AccountStatus;

public record GetAccountResponse(
    String uuid,
    String firstName,
    String lastName,
    String email,
    String fiscalCode,
    String phoneNumber,
    String city,
    String state,
    String country,
    AccountStatus status
){}
