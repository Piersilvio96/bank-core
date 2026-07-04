package it.bank.bankcore.ledger.domain.model;

import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedgerEntryEntity extends BaseEntity {
    private String accountId;
    private String paymentId;
    @Enumerated(EnumType.STRING)
    private LedgerType type; // "DEBIT" or "CREDIT"
    private String currency;
    @Column(columnDefinition = "DECIMAL(19,2)")
    private BigDecimal amount;

}
