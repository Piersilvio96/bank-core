package it.bank.bankcore.account.application.command;

public record CreateAccountCommand(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String fiscalCode,
        String city,
        String state,
        String country,
        String currency
) {
}
