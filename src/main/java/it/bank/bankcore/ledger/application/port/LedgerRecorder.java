package it.bank.bankcore.ledger.application.port;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;

public interface LedgerRecorder {
    void recordDeposit(RecordDepositLedgerCommand command);
    void recordWithdraw(RecordWithdrawLedgerCommand command);
    void recordTransfer(RecordTransferLedgerCommand command);
}
