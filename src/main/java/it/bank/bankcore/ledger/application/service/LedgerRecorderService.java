package it.bank.bankcore.ledger.application.service;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordReversalLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryDepositUseCase;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryRevertUseCase;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryTransferUseCase;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryWithdrawUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerRecorderService implements LedgerRecorder {

    private final LedgerEntryDepositUseCase ledgerEntryDepositUseCase;
    private final LedgerEntryWithdrawUseCase ledgerEntryWithdrawUseCase;
    private final LedgerEntryTransferUseCase ledgerEntryTransferUseCase;
    private final LedgerEntryRevertUseCase ledgerEntryRevertUseCase;

    @Override
    public void recordDeposit(RecordDepositLedgerCommand command) {
        ledgerEntryDepositUseCase.execute(command);
    }

    @Override
    public void recordWithdraw(RecordWithdrawLedgerCommand command) {
        ledgerEntryWithdrawUseCase.execute(command);
    }

    @Override
    public void recordTransfer(RecordTransferLedgerCommand command) {
        ledgerEntryTransferUseCase.execute(command);
    }

    @Override
    public void recordReversal(RecordReversalLedgerCommand command) {
        ledgerEntryRevertUseCase.execute(command);
    }


}
