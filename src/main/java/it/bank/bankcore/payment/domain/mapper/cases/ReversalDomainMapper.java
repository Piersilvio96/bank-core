package it.bank.bankcore.payment.domain.mapper.cases;

import it.bank.bankcore.payment.application.command.ReversalCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class ReversalDomainMapper {

    public Payment toDomain(ReversalCommand command, Payment toBeReversedPayment) {
        return Payment.builder()
                .sourceAccountUuid(toBeReversedPayment.getTargetAccountUuid())
                .targetAccountUuid(toBeReversedPayment.getSourceAccountUuid())
                .amount(toBeReversedPayment.getAmount())
                .currency(toBeReversedPayment.getCurrency())
                .reason(command.reason())
                .requestCode(command.requestCode())
                .status(PaymentStatus.REVERSED)
                .build();
    }

}
