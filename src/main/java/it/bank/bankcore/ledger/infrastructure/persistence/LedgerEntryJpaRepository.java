package it.bank.bankcore.ledger.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LedgerEntryJpaRepository extends JpaRepository<LedgerEntryJpaEntity, Long>, JpaSpecificationExecutor<LedgerEntryJpaEntity> {

    List<LedgerEntryJpaEntity> findByPaymentId(String paymentId);
}
