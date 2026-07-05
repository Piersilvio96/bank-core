package it.bank.bankcore.account.api.response;

import java.math.BigDecimal;


public record GetAccountBalanceResponse (
    BigDecimal balance,
    String currency
){}
