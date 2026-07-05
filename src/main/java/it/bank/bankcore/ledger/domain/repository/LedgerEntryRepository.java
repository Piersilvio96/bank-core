package it.bank.bankcore.ledger.domain.repository;

import it.bank.bankcore.ledger.domain.model.LedgerEntry;

public interface LedgerEntryRepository {
    LedgerEntry save(LedgerEntry entry);
}
