package it.bank.bankcore.account.application.result;

import it.bank.bankcore.account.domain.enums.AccountStatus;


public record GetAccountResult (
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
) {}
