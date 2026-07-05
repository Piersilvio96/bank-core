package it.bank.bankcore.account.application.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountCommand(
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @NotEmpty
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
        String email,
        @NotEmpty
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        String phoneNumber,
        @NotEmpty
        String fiscalCode,
        @NotEmpty
        String city,
        @NotEmpty
        String state,
        @NotEmpty
        String country,
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency
) {
}
