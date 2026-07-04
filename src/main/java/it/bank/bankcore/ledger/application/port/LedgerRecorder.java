package it.bank.bankcore.ledger.application.port;

import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;

public interface LedgerRecorder {
    void recordTransfer(RecordTransferLedgerCommand command);
}
