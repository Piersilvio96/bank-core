package it.bank.bankcore.ledger.infrastructure.service;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaEntity;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LedgerRecordingServiceTest {

    @Mock
    private LedgerEntryJpaRepository ledgerEntryRepository;

    @InjectMocks
    private LedgerRecordingService service;

    @Test
    void recordDeposit_shouldSaveCreditEntry() {
        var command = new RecordDepositLedgerCommand("acc-1", "pay-1", new BigDecimal("12.50"), "EUR", "Deposit");

        service.recordDeposit(command);

        var captor = ArgumentCaptor.forClass(LedgerEntryJpaEntity.class);
        verify(ledgerEntryRepository).save(captor.capture());

        var entry = captor.getValue();
        assertEquals("acc-1", entry.getAccountId());
        assertEquals("pay-1", entry.getPaymentId());
        assertEquals(LedgerType.CREDIT, entry.getType());
    }

    @Test
    void recordWithdraw_shouldSaveDebitEntry() {
        var command = new RecordWithdrawLedgerCommand("acc-2", "pay-2", new BigDecimal("5.00"), "EUR", "Withdraw");

        service.recordWithdraw(command);

        var captor = ArgumentCaptor.forClass(LedgerEntryJpaEntity.class);
        verify(ledgerEntryRepository).save(captor.capture());

        var entry = captor.getValue();
        assertEquals("acc-2", entry.getAccountId());
        assertEquals("pay-2", entry.getPaymentId());
        assertEquals(LedgerType.DEBIT, entry.getType());
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

        var captor = ArgumentCaptor.forClass(LedgerEntryJpaEntity.class);
        verify(ledgerEntryRepository, times(2)).save(captor.capture());

        var entries = captor.getAllValues();
        assertEquals("source-acc", entries.get(0).getAccountId());
        assertEquals(LedgerType.DEBIT, entries.get(0).getType());
        assertEquals("target-acc", entries.get(1).getAccountId());
        assertEquals(LedgerType.CREDIT, entries.get(1).getType());
    }
}

