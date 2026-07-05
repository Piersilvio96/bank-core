package it.bank.bankcore.ledger.application.usecase;

import it.bank.bankcore.ledger.application.command.RecordWithdrawLedgerCommand;
import it.bank.bankcore.ledger.domain.mapper.LedgerDomainMapper;
import it.bank.bankcore.ledger.domain.repository.LedgerEntryRepository;
import it.bank.bankcore.shared.application.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerEntryWithdrawUseCase implements UseCase<RecordWithdrawLedgerCommand, Void> {
    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerDomainMapper ledgerDomainMapper;

    @Override
    public Void execute(RecordWithdrawLedgerCommand input) {
        var entry = ledgerDomainMapper.toDomain(input);
        ledgerEntryRepository.save(entry);
        return null;
    }
}
