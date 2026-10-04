package it.bank.bankcore.payment.infrastructure.persistence;

import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import it.bank.bankcore.payment.infrastructure.persistence.mapper.PaymentJpaMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import java.sql.SQLException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRepositoryImplTest {
    private final PaymentJpaRepository jpa = mock(PaymentJpaRepository.class);
    private final PaymentJpaMapper mapper = mock(PaymentJpaMapper.class);
    private final PaymentRepositoryImpl repository = new PaymentRepositoryImpl(jpa, mapper);

    @Test
    void translatesOnlyRequestCodeUniqueViolationAndPreservesCause() {
        var failure = failure("23505", "payments_request_code_key");
        arrange(failure);
        var thrown = assertThrows(PaymentCodeAlreadyExists.class,
                () -> repository.save(Payment.builder().requestCode("request-001").build()));
        assertSame(failure, thrown.getCause());
    }

    @Test
    void preservesOtherUniqueViolations() {
        var failure = failure("23505", "payments_uuid_key");
        arrange(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(Payment.builder().requestCode("request-001").build())));
    }

    @Test
    void preservesForeignKeyAndNotNullViolations() {
        for (String state : new String[]{"23503", "23502"}) {
            var failure = failure(state, "payments_request_code_key");
            arrange(failure);
            assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                    () -> repository.save(Payment.builder().requestCode("request-001").build())));
        }
    }

    @Test
    void originalPaymentLookupRetainsPessimisticLock() {
        when(jpa.findByUuidForUpdate("original")).thenReturn(Optional.empty());
        assertTrue(repository.findByPaymentId("original").isEmpty());
        verify(jpa).findByUuidForUpdate("original");
        verify(jpa, never()).findByUuid(any());
    }

    private void arrange(DataIntegrityViolationException failure) {
        var entity = PaymentJpaEntity.builder().build();
        when(mapper.toEntity(any())).thenReturn(entity);
        when(jpa.saveAndFlush(entity)).thenThrow(failure);
    }

    private DataIntegrityViolationException failure(String state, String constraint) {
        return new DataIntegrityViolationException("insert failed",
                new ConstraintViolationException("constraint violation", new SQLException("failure", state), constraint));
    }
}
