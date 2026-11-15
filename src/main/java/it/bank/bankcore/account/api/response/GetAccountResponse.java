package it.bank.bankcore.account.api.response;

import it.bank.bankcore.account.domain.enums.AccountStatus;

import java.math.BigDecimal;

public record GetAccountResponse(
    String uuid,
    String userUuid,
    BigDecimal balance,
    String currency,
    AccountStatus status
){}
