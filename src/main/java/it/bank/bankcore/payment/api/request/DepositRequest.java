package it.bank.bankcore.payment.api.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DepositRequest (
    @NotEmpty(message = "Account UUID cannot be empty")
    String accountUuid,
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    String currency
){}