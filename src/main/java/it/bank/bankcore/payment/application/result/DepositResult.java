package it.bank.bankcore.payment.application.result;

import java.math.BigDecimal;

public record DepositResult(
        String paymentId,
        BigDecimal amount,
        String currency,
        boolean created
) {
}
