package it.bank.bankcore.ledger.application.service;

import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.application.port.LedgerRecorder;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaEntity;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerRecordingService implements LedgerRecorder {

    private final LedgerEntryJpaRepository ledgerEntryRepository;

    @Override
    public void recordTransfer(RecordTransferLedgerCommand command) {
        var entry = LedgerEntryJpaEntity.builder()
                .accountId(command.accountId())
                .paymentId(command.paymentId())
                .type(command.type())
                .currency(command.currency())
                .amount(command.amount())
                .build();

        ledgerEntryRepository.save(entry);
    }
}
