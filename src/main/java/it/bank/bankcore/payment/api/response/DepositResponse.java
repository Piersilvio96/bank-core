package it.bank.bankcore.payment.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositResponse {
    private String paymentId;
    private BigDecimal amount;
    private String currency;
}
