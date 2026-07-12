package it.bank.bankcore.payment.api.response;

import java.math.BigDecimal;

public record ReversalResponse(
        String paymentId,
        String reversedPaymentId,
        BigDecimal amount,
        String currency,
        boolean created
) {
}
