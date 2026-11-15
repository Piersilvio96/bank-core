package it.bank.bankcore.account.application.command;

public record CreateAccountCommand(
        String userUuid,
        String currency
) {
}
