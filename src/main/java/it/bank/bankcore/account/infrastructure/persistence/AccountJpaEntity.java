package it.bank.bankcore.account.infrastructure.persistence;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.shared.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountJpaEntity extends BaseEntity {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String fiscalCode;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String country;
    @Column(columnDefinition = "DECIMAL(19,2)")
    private BigDecimal balance;
    @Column(columnDefinition = "CHAR(3)", nullable = false)
    private String currency;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = AccountStatus.ACTIVE;
        }
        if (ObjectUtils.isEmpty(currency)) {
            currency = "EUR";
        }
        balance = BigDecimal.ZERO;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AccountJpaEntity that = (AccountJpaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
