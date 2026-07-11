package it.bank.bankcore.payment.infrastructure.persistence;

import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.shared.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaymentJpaEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountJpaEntity sourceAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountJpaEntity targetAccount;
    @Column(columnDefinition = "DECIMAL(19,2)")
    private BigDecimal amount;
    @Column(columnDefinition = "TEXT")
    private String reason;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String currency;
    @Column(unique = true)
    private String requestCode;

}
