package it.bank.bankcore.ledger.application.command;

import it.bank.bankcore.ledger.domain.enums.LedgerType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecordTransferLedgerCommand (
        @NotEmpty String accountId,
        @NotEmpty String paymentId,
        @NotEmpty LedgerType type,
        @NotEmpty @Positive BigDecimal amount,
        @NotEmpty String currency,
        @NotEmpty String reason
){}
