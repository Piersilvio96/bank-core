package it.bank.bankcore.payment.application.command;

import java.math.BigDecimal;

public record TransferCommand(
		String sourceAccountUuid,
		String targetAccountUuid,
		BigDecimal amount,
		String currency,
		String reason
) {
}
