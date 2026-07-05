package it.bank.bankcore.ledger.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryJpaRepository extends JpaRepository<LedgerEntryJpaEntity, Long>, JpaSpecificationExecutor<LedgerEntryJpaEntity> {
}
