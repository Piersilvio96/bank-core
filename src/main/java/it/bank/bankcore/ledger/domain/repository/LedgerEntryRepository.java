package it.bank.bankcore.ledger.domain.repository;

import it.bank.bankcore.ledger.domain.model.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, Long>, JpaSpecificationExecutor<LedgerEntryEntity> {
}
