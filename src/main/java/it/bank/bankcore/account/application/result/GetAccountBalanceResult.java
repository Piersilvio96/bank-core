package it.bank.bankcore.account.application.result;

import java.math.BigDecimal;

public record GetAccountBalanceResult(
    BigDecimal balance,
    String currency
) {}
