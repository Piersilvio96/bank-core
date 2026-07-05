package it.bank.bankcore.ledger.application.usecase;

import it.bank.bankcore.ledger.application.command.RecordTransferLedgerCommand;
import it.bank.bankcore.ledger.domain.mapper.LedgerDomainMapper;
import it.bank.bankcore.ledger.domain.repository.LedgerEntryRepository;
import it.bank.bankcore.shared.application.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerEntryTransferUseCase implements UseCase <RecordTransferLedgerCommand, Void> {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerDomainMapper ledgerDomainMapper;

    @Override
    public Void execute(RecordTransferLedgerCommand input) {
        var sourceEntry = ledgerDomainMapper.toDomain(input, true);
        var targetEntry = ledgerDomainMapper.toDomain(input, false);
        ledgerEntryRepository.save(sourceEntry);
        ledgerEntryRepository.save(targetEntry);
        return null;
    }
}
