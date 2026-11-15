package it.bank.bankcore.account.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest (
    @NotEmpty
    String userUuid,
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    String currency
) {}
