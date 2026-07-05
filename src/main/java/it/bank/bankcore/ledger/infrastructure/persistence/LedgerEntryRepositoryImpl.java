package it.bank.bankcore.ledger.infrastructure.persistence;

import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import it.bank.bankcore.ledger.domain.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LedgerEntryRepositoryImpl implements LedgerEntryRepository {

    private final LedgerEntryJpaMapper ledgerEntryJpaMapper;
    private final LedgerEntryJpaRepository ledgerEntryJpaRepository;


    @Override
    public LedgerEntry save(LedgerEntry entry) {
        var ledgerEntryJpa = ledgerEntryJpaMapper.toEntity(entry);
        ledgerEntryJpaRepository.save(ledgerEntryJpa);
        return ledgerEntryJpaMapper.toDomain(ledgerEntryJpaRepository.save(ledgerEntryJpa));
    }
}
