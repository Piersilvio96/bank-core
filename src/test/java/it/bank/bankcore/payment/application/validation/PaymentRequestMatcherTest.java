package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.payment.application.command.*;
import it.bank.bankcore.payment.domain.enums.PaymentType;
import it.bank.bankcore.payment.domain.exception.RequestCodeConflictException;
import it.bank.bankcore.payment.domain.model.Payment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestMatcherTest {
    private Payment deposit() {
        return Payment.builder().type(PaymentType.DEPOSIT).targetAccountUuid("account")
                .amount(new BigDecimal("10.00")).currency("EUR").build();
    }
    @Test
    void acceptsEquivalentDecimalAmounts() {
        var payment = deposit();
        assertSame(payment, PaymentRequestMatcher.match(payment,
                new DepositCommand("account", new BigDecimal("10.000"), "EUR", "request-001")));
    }
    @Test
    void rejectsChangedAmountAccountCurrencyAndOperation() {
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(deposit(),
                new DepositCommand("account", new BigDecimal("11"), "EUR", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(deposit(),
                new DepositCommand("other", new BigDecimal("10"), "EUR", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(deposit(),
                new DepositCommand("account", new BigDecimal("10"), "USD", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(deposit(),
                new WithdrawCommand("account", new BigDecimal("10"), "EUR", "request-001")));
    }
    @Test
    void comparesTransferAccountsAndNormalizedReason() {
        var payment = Payment.builder().type(PaymentType.TRANSFER).sourceAccountUuid("source")
                .targetAccountUuid("target").amount(BigDecimal.TEN).currency("EUR").reason("rent").build();
        assertSame(payment, PaymentRequestMatcher.match(payment,
                new TransferCommand("source", "target", BigDecimal.TEN, "EUR", " rent ", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(payment,
                new TransferCommand("target", "source", BigDecimal.TEN, "EUR", "rent", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(payment,
                new TransferCommand("source", "target", BigDecimal.TEN, "EUR", "food", "request-001")));
    }
    @Test
    void comparesReversalOriginalAndReasonWithoutDereferencingOtherPaymentTypes() {
        var payment = Payment.builder().type(PaymentType.REVERSAL).reason(" correction ")
                .reversedPayment(Payment.builder().uuid("original").build()).build();
        assertSame(payment, PaymentRequestMatcher.match(payment,
                new ReversalCommand("original", "correction", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(payment,
                new ReversalCommand("other", "correction", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(payment,
                new ReversalCommand("original", "different", "request-001")));
        assertThrows(RequestCodeConflictException.class, () -> PaymentRequestMatcher.match(deposit(),
                new ReversalCommand("original", "correction", "request-001")));
    }
}
