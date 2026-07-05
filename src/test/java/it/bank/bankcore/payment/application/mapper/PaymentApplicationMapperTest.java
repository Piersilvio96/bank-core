package it.bank.bankcore.payment.application.mapper;

import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentApplicationMapperTest {

    private final PaymentApplicationMapper mapper = new PaymentApplicationMapper();

    @Test
    void toDepositResult_shouldMapPaymentFields() {
        var payment = samplePayment();

        var result = mapper.toDepositResult(payment);

        assertEquals(payment.getUuid(), result.paymentId());
        assertEquals(0, payment.getAmount().compareTo(result.amount()));
        assertEquals(payment.getCurrency(), result.currency());
    }

    @Test
    void toWithdrawResult_shouldMapPaymentFields() {
        var payment = samplePayment();

        var result = mapper.toWithdrawResult(payment);

        assertEquals(payment.getUuid(), result.paymentId());
        assertEquals(0, payment.getAmount().compareTo(result.amount()));
        assertEquals(payment.getCurrency(), result.currency());
    }

    @Test
    void toTransferResult_shouldMapPaymentFields() {
        var payment = samplePayment();

        var result = mapper.toTransferResult(payment);

        assertEquals(payment.getUuid(), result.paymentId());
        assertEquals(0, payment.getAmount().compareTo(result.amount()));
        assertEquals(payment.getCurrency(), result.currency());
    }

    private Payment samplePayment() {
        return Payment.builder()
                .uuid("pay-uuid")
                .sourceAccountUuid("source-uuid")
                .targetAccountUuid("target-uuid")
                .amount(new BigDecimal("12.50"))
                .reason("Transfer")
                .status(PaymentStatus.COMPLETED)
                .currency("EUR")
                .build();
    }
}

