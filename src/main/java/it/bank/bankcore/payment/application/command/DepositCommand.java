package it.bank.bankcore.payment.application.command;

import java.math.BigDecimal;

public record DepositCommand(
		String accountUuid,
		BigDecimal amount,
		String currency
) {
}
