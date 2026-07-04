package it.bank.bankcore.payment.domain.model;

import it.bank.bankcore.account.domain.model.AccountEntity;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity sourceAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity targetAccount;
    @Column(columnDefinition = "DECIMAL(19,2)")
    private BigDecimal amount;
    @Column(columnDefinition = "TEXT")
    private String reason;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String currency;

}
