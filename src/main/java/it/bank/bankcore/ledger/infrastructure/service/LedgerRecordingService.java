package it.bank.bankcore.ledger.infrastructure.service;

import it.bank.bankcore.ledger.application.command.RecordDepositLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaEntity;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerRecordingService implements LedgerRecorder {

    private final LedgerEntryJpaRepository ledgerEntryRepository;

    @Override
    public void recordDeposit(RecordDepositLedgerCommand command) {
        var entry = LedgerEntryJpaEntity.builder()
                .accountId(command.accountId())
                .paymentId(command.paymentId())
                .type(LedgerType.CREDIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();

        ledgerEntryRepository.save(entry);
    }

    @Override
    public void recordWithdraw(RecordWithdrawLedgerCommand command) {
        var entry = LedgerEntryJpaEntity.builder()
                .accountId(command.accountId())
                .paymentId(command.paymentId())
                .type(LedgerType.DEBIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();

        ledgerEntryRepository.save(entry);
    }

    @Override
    public void recordTransfer(RecordTransferLedgerCommand command) {
        var sourceEntry = LedgerEntryJpaEntity.builder()
                .accountId(command.sourceAccountId())
                .paymentId(command.paymentId())
                .type(LedgerType.DEBIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();

        var targetEntry = LedgerEntryJpaEntity.builder()
                .accountId(command.targetAccountId())
                .paymentId(command.paymentId())
                .type(LedgerType.CREDIT)
                .currency(command.currency())
                .amount(command.amount())
                .build();

        ledgerEntryRepository.save(sourceEntry);
        ledgerEntryRepository.save(targetEntry);
    }
}

