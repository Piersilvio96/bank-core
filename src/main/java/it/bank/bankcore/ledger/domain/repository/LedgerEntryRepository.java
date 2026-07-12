package it.bank.bankcore.ledger.domain.repository;

import it.bank.bankcore.ledger.domain.model.LedgerEntry;

import java.util.List;

public interface LedgerEntryRepository {
    LedgerEntry save(LedgerEntry entry);
    List<LedgerEntry> findByPaymentId(String paymentId);
    List<LedgerEntry> saveAll(List<LedgerEntry> entries);
}
