package it.bank.bankcore.payment.application.command;

import java.math.BigDecimal;

public record WithdrawCommand(
		String accountUuid,
		BigDecimal amount,
		String currency
) {
}
