package it.bank.bankcore.payment.domain.mapper.cases;

import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class TransferDomainMapper implements DomainMapper <TransferCommand, Payment> {

    private static final String TRANSFER_REASON = "Transfer";

    @Override
    public Payment toDomain(TransferCommand command) {
        return Payment.builder()
                .sourceAccountUuid(command.sourceAccountUuid())
                .targetAccountUuid(command.targetAccountUuid())
                .amount(command.amount())
                .currency(command.currency())
                .reason(ObjectUtils.isEmpty(command.reason()) ? TRANSFER_REASON : command.reason())
                .status(PaymentStatus.PENDING)
                .build();
    }
}
