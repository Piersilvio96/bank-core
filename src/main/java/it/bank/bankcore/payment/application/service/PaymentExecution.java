package it.bank.bankcore.payment.application.service;

import it.bank.bankcore.payment.infrastructure.exception.PaymentCodeAlreadyExists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.function.Supplier;

@Service
public class PaymentExecution {
    private final TransactionTemplate transaction;
    public PaymentExecution(PlatformTransactionManager manager) {
        transaction = new TransactionTemplate(manager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }
    public <T> T execute(Supplier<T> operation, Supplier<T> replay) {
        try {
            return transaction.execute(status -> operation.get());
        } catch (PaymentCodeAlreadyExists duplicate) {
            // All changes have rolled back before the existing payment is read.
            return transaction.execute(status -> replay.get());
        }
    }
}
