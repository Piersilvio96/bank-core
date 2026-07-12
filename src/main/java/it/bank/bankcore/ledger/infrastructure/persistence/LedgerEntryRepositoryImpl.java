package it.bank.bankcore.ledger.infrastructure.persistence;

import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import it.bank.bankcore.ledger.domain.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<LedgerEntry> findByPaymentId(String paymentId) {
        var ledgerEntries = ledgerEntryJpaRepository.findByPaymentId(paymentId);
        return ledgerEntries.stream()
                .map(ledgerEntryJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<LedgerEntry> saveAll(List<LedgerEntry> entries) {
        var ledgerEntryJpas = entries.stream()
                .map(ledgerEntryJpaMapper::toEntity)
                .toList();
        ledgerEntryJpaRepository.saveAll(ledgerEntryJpas);
        return ledgerEntryJpas.stream()
                .map(ledgerEntryJpaMapper::toDomain)
                .toList();
    }


}
