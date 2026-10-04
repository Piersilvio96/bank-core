package it.bank.bankcore.payment.application.service;

import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentExecutionTest {
    @Test
    void rollsBackBeforeReplayingInAFreshTransaction() {
        var manager = mock(PlatformTransactionManager.class);
        var failed = new SimpleTransactionStatus();
        var replayed = new SimpleTransactionStatus();
        when(manager.getTransaction(any())).thenReturn(failed, replayed);
        Supplier<String> replay = mock(Supplier.class);
        when(replay.get()).thenReturn("existing-payment");
        var execution = new PaymentExecution(manager);

        assertEquals("existing-payment", execution.execute(
                () -> { throw new PaymentCodeAlreadyExists("duplicate"); }, replay));

        var order = inOrder(manager, replay);
        order.verify(manager).getTransaction(any());
        order.verify(manager).rollback(failed);
        order.verify(manager).getTransaction(any());
        order.verify(replay).get();
        order.verify(manager).commit(replayed);
    }

    @Test
    void doesNotReplayUnrelatedIntegrityFailures() {
        var manager = mock(PlatformTransactionManager.class);
        var status = new SimpleTransactionStatus();
        when(manager.getTransaction(any())).thenReturn(status);
        Supplier<String> replay = mock(Supplier.class);
        var failure = new DataIntegrityViolationException("foreign key");
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> new PaymentExecution(manager).execute(() -> { throw failure; }, replay)));
        verify(manager).rollback(status);
        verifyNoInteractions(replay);
    }
}
