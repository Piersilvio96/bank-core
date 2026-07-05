package it.bank.bankcore.ledger.infrastructure.service;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.application.service.LedgerRecorderService;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryDepositUseCase;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryTransferUseCase;
import it.bank.bankcore.ledger.application.usecase.LedgerEntryWithdrawUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LedgerRecordingServiceTest {

    @Mock
    private LedgerEntryDepositUseCase ledgerEntryDepositUseCase;

    @Mock
    private LedgerEntryWithdrawUseCase ledgerEntryWithdrawUseCase;

    @Mock
    private LedgerEntryTransferUseCase ledgerEntryTransferUseCase;

    @InjectMocks
    private LedgerRecorderService service;

    @Test
    void recordDeposit_shouldSaveCreditEntry() {
        var command = new RecordDepositLedgerCommand("acc-1", "pay-1", new BigDecimal("12.50"), "EUR", "Deposit");

        service.recordDeposit(command);

        verify(ledgerEntryDepositUseCase).execute(command);
    }

    @Test
    void recordWithdraw_shouldSaveDebitEntry() {
        var command = new RecordWithdrawLedgerCommand("acc-2", "pay-2", new BigDecimal("5.00"), "EUR", "Withdraw");

        service.recordWithdraw(command);

        verify(ledgerEntryWithdrawUseCase).execute(command);
    }

    @Test
    void recordTransfer_shouldSaveDebitAndCreditEntries() {
        var command = new RecordTransferLedgerCommand(
                "source-acc",
                "target-acc",
                "pay-3",
                new BigDecimal("20.00"),
                "EUR",
                "Transfer"
        );

        service.recordTransfer(command);

        verify(ledgerEntryTransferUseCase).execute(command);
    }
}

