package it.bank.bankcore.account.application.result;

import it.bank.bankcore.account.domain.enums.AccountStatus;

import java.math.BigDecimal;

public record GetAccountResult (
    String uuid,
    String userUuid,
    BigDecimal balance,
    String currency,
    AccountStatus status
) {}
