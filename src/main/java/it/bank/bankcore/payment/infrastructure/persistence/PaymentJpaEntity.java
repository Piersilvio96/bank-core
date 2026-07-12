package it.bank.bankcore.payment.infrastructure.persistence;

import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.shared.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(columnDefinition = "VARCHAR(3)", nullable = false)
    private String currency;
    @Column(unique = true, nullable = false)
    private String requestCode;
    @OneToOne(fetch = FetchType.EAGER)
    private PaymentJpaEntity reversedPayment;
    @OneToOne(fetch = FetchType.EAGER)
    private PaymentJpaEntity reversalPayment;
}
