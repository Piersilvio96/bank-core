package it.bank.bankcore.ledger.infrastructure.persistence;

import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.shared.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LedgerEntryJpaEntity extends BaseEntity {
    private String accountId;
    private String paymentId;
    @Enumerated(EnumType.STRING)
    private LedgerType type; // "DEBIT" or "CREDIT"
    private String currency;
    @Column(columnDefinition = "DECIMAL(19,2)")
    private BigDecimal amount;

}
