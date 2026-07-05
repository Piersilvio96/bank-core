package it.bank.bankcore.payment.application.mapper;

import it.bank.bankcore.payment.application.result.DepositResult;
import it.bank.bankcore.payment.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentApplicationMapper {

    public DepositResult toDepositResult(Payment payment) {
        return new DepositResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency()
        );
    }
}

