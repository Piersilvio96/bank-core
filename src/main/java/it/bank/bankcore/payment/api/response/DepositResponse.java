package it.bank.bankcore.payment.api.response;

import java.math.BigDecimal;

public record DepositResponse(
        String paymentId,
        BigDecimal amount,
        String currency,
        boolean created
) {
}
