package it.bank.bankcore.ledger.application.command;

import jakarta.validation.constraints.NotEmpty;

public record RecordReversalLedgerCommand(
        @NotEmpty String paymentId,
        @NotEmpty String reversalId,
        @NotEmpty String reason
) { }
