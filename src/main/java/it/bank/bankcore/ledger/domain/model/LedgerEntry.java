package it.bank.bankcore.ledger.domain.model;

import it.bank.bankcore.ledger.domain.enums.LedgerType;
import it.bank.bankcore.shared.domain.Base;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LedgerEntry extends Base {
    private String accountId;
    private String paymentId;
    private LedgerType type;
    private String currency;
    private BigDecimal amount;
}
