package it.bank.bankcore.payment.infrastructure.persistence.mapper;

import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaRepository;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.infrastructure.persistence.PaymentJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentJpaMapperTest {

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @InjectMocks
    private PaymentJpaMapper mapper;

    @Test
    void toDomain_shouldMapEntityToDomain() {
        var source = AccountJpaEntity.builder().build();
        source.setUuid("source-uuid");

        var target = AccountJpaEntity.builder().build();
        target.setUuid("target-uuid");

        var entity = PaymentJpaEntity.builder()
                .sourceAccount(source)
                .targetAccount(target)
                .amount(new BigDecimal("30.00"))
                .reason("Deposit")
                .status(PaymentStatus.COMPLETED)
                .currency("EUR")
                .build();
        entity.setUuid("payment-uuid");

        var result = mapper.toDomain(entity);

        assertEquals("payment-uuid", result.getUuid());
        assertEquals("source-uuid", result.getSourceAccountUuid());
        assertEquals("target-uuid", result.getTargetAccountUuid());
        assertEquals(0, result.getAmount().compareTo(new BigDecimal("30.00")));
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
    }

    @Test
    void toEntity_shouldResolveAccountsAndMapFields() {
        var sourceEntity = AccountJpaEntity.builder().build();
        sourceEntity.setUuid("source-uuid");

        var targetEntity = AccountJpaEntity.builder().build();
        targetEntity.setUuid("target-uuid");

        var payment = Payment.builder()
                .sourceAccountUuid("source-uuid")
                .targetAccountUuid("target-uuid")
                .amount(new BigDecimal("50.00"))
                .reason("Transfer")
                .status(PaymentStatus.PENDING)
                .currency("EUR")
                .build();

        when(accountJpaRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sourceEntity));
        when(accountJpaRepository.findByUuid("target-uuid")).thenReturn(Optional.of(targetEntity));

        var entity = mapper.toEntity(payment);

        assertEquals("source-uuid", entity.getSourceAccount().getUuid());
        assertEquals("target-uuid", entity.getTargetAccount().getUuid());
        assertEquals(0, entity.getAmount().compareTo(new BigDecimal("50.00")));
        assertEquals(PaymentStatus.PENDING, entity.getStatus());
    }

    @Test
    void toEntity_shouldThrowWhenTargetAccountMissing() {
        var payment = Payment.builder()
                .targetAccountUuid("missing-target")
                .amount(new BigDecimal("10.00"))
                .reason("Deposit")
                .status(PaymentStatus.PENDING)
                .currency("EUR")
                .build();

        when(accountJpaRepository.findByUuid("missing-target")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> mapper.toEntity(payment));
    }
}

