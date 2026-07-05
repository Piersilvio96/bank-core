package it.bank.bankcore.account.application.result;

import java.math.BigDecimal;


public record CreateAccountResult(
    String uuid,
    String firstName,
    String lastName,
    String email,
    String fiscalCode,
    String phoneNumber,
    String city,
    String state,
    String country,
    BigDecimal balance,
    String currency
) {}
