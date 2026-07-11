package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.account.domain.model.Account;

public record TransferValidationResult(Account sourceAccount, Account targetAccount) {
}

