package it.bank.bankcore.ledger.application.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecordDepositLedgerCommand(
        @NotEmpty String accountId,
        @NotEmpty String paymentId,
        @NotEmpty @Positive BigDecimal amount,
        @NotEmpty String currency,
        @NotEmpty String reason
) {
}
