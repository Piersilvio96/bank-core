package it.bank.bankcore.account.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAccountBalanceResponse {
    private BigDecimal balance;
    private String currency;
}
