package it.bank.bankcore.payment.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequest(
    @NotBlank(message = "Account UUID cannot be blank")
    String accountUuid,
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,
    @NotBlank(message = "Currency cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter uppercase ISO code")
    String currency,
    @NotBlank(message = "Request code cannot be blank")
    @Pattern(regexp = "^[A-Za-z0-9._:-]{8,64}$", message = "Request code format is invalid")
    String requestCode
){}