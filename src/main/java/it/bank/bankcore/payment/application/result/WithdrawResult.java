package it.bank.bankcore.payment.application.result;

import java.math.BigDecimal;

public record WithdrawResult(
        String paymentId,
        BigDecimal amount,
        String currency
) {
}

