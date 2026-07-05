package it.bank.bankcore.payment.domain.model;

import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.shared.domain.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Payment extends Base {

    private String sourceAccountUuid;
    private String targetAccountUuid;
    private BigDecimal amount;
    private String reason;
    private PaymentStatus status;
    private String currency;
}

