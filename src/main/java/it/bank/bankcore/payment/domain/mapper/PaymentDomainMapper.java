package it.bank.bankcore.payment.domain.mapper;

import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentDomainMapper {

    private static final String DEPOSIT_REASON = "Deposit";

    public Payment toDomain(DepositCommand command, String targetCurrency) {
        return Payment.builder()
                .sourceAccountUuid(null)
                .targetAccountUuid(command.accountUuid())
                .amount(command.amount())
                .currency(targetCurrency)
                .reason(DEPOSIT_REASON)
                .status(PaymentStatus.PENDING)
                .build();
    }
}

