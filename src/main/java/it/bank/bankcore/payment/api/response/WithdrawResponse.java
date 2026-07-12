package it.bank.bankcore.payment.api.response;

import java.math.BigDecimal;

public record WithdrawResponse(
        String paymentId,
        BigDecimal amount,
        String currency,
        boolean created
) {
}
