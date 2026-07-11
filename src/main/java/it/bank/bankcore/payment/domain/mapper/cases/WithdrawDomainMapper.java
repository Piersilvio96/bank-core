package it.bank.bankcore.payment.domain.mapper.cases;

import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class WithdrawDomainMapper implements DomainMapper <WithdrawCommand, Payment> {

    private static final String WITHDRAW_REASON = "Withdraw";

    @Override
    public Payment toDomain(WithdrawCommand command) {
        return Payment.builder()
                .sourceAccountUuid(null)
                .targetAccountUuid(command.accountUuid())
                .amount(command.amount())
                .currency(command.currency())
                .reason(WITHDRAW_REASON)
                .status(PaymentStatus.PENDING)
                .requestCode(command.requestCode())
                .build();
    }
}
