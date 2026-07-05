package it.bank.bankcore.ledger.infrastructure.persistence;

import it.bank.bankcore.ledger.domain.model.LedgerEntry;
import it.bank.bankcore.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Component;

@Component
public class LedgerEntryJpaMapper implements JpaMapper<LedgerEntry, LedgerEntryJpaEntity> {

    @Override
    public LedgerEntry toDomain(LedgerEntryJpaEntity entity) {
        return LedgerEntry.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .paymentId(entity.getPaymentId())
                .type(entity.getType())
                .currency(entity.getCurrency())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public LedgerEntryJpaEntity toEntity(LedgerEntry domain) {
        return LedgerEntryJpaEntity.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .paymentId(domain.getPaymentId())
                .type(domain.getType())
                .currency(domain.getCurrency())
                .amount(domain.getAmount())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
