package it.bank.bankcore.payment.application.result;

import java.math.BigDecimal;

public record ReversalResult(
        String paymentId,
        String reversedPaymentId,
        BigDecimal amount,
        String currency,
        boolean created
) {
}
