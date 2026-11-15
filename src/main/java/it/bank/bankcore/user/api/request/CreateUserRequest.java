package it.bank.bankcore.user.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
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
    String country
) {}
