package it.bank.bankcore.user.application.command;

public record CreateUserCommand(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String fiscalCode,
        String city,
        String state,
        String country
) {
}
