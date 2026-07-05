package it.bank.bankcore.payment.domain.mapper.cases;

import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class DepositDomainMapper implements DomainMapper <DepositCommand, Payment> {

    private static final String DEPOSIT_REASON = "Deposit";

    @Override
    public Payment toDomain(DepositCommand command) {
        return Payment.builder()
                .sourceAccountUuid(null)
                .targetAccountUuid(command.accountUuid())
                .amount(command.amount())
                .currency(command.currency())
                .reason(DEPOSIT_REASON)
                .status(PaymentStatus.PENDING)
                .build();
    }
}
