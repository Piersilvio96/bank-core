package it.bank.bankcore.payment.domain.mapper.cases;

import it.bank.bankcore.payment.domain.enums.PaymentType;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import it.bank.bankcore.shared.domain.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class TransferDomainMapper implements DomainMapper <TransferCommand, Payment> {

    private static final String TRANSFER_REASON = "Transfer";

    @Override
    public Payment toDomain(TransferCommand command) {
        return Payment.builder()
                .type(PaymentType.TRANSFER)
                .sourceAccountUuid(command.sourceAccountUuid())
                .targetAccountUuid(command.targetAccountUuid())
                .amount(command.amount())
                .currency(command.currency())
                .reason(command.reason() == null || command.reason().isBlank() ? TRANSFER_REASON : command.reason().trim())
                .status(PaymentStatus.PENDING)
                .requestCode(command.requestCode())
                .build();
    }
}
