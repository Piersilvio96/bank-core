package it.bank.bankcore.payment.infrastructure.persistence.mapper;

import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaRepository;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.infrastructure.persistence.PaymentJpaEntity;
import it.bank.bankcore.shared.infrastructure.JpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentJpaMapper implements JpaMapper<Payment, PaymentJpaEntity> {

    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Payment toDomain(PaymentJpaEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .sourceAccountUuid(entity.getSourceAccount() != null ? entity.getSourceAccount().getUuid() : null)
                .targetAccountUuid(entity.getTargetAccount() != null ? entity.getTargetAccount().getUuid() : null)
                .amount(entity.getAmount())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .currency(entity.getCurrency())
                .requestCode(entity.getRequestCode())
                .build();
    }

    @Override
    public PaymentJpaEntity toEntity(Payment domain) {
        AccountJpaEntity source = null;
        if (domain.getSourceAccountUuid() != null) {
            source = accountJpaRepository.findByUuid(domain.getSourceAccountUuid())
                    .orElseThrow(() -> new AccountNotFoundException(domain.getSourceAccountUuid()));
        }

        AccountJpaEntity target = null;
        if (domain.getTargetAccountUuid() != null) {
            target = accountJpaRepository.findByUuid(domain.getTargetAccountUuid())
                    .orElseThrow(() -> new AccountNotFoundException(domain.getTargetAccountUuid()));
        }

        return PaymentJpaEntity.builder()
                .sourceAccount(source)
                .targetAccount(target)
                .amount(domain.getAmount())
                .reason(domain.getReason())
                .status(domain.getStatus())
                .currency(domain.getCurrency())
                .requestCode(domain.getRequestCode())
                .build();
    }
}

