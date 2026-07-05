package it.bank.bankcore.payment.domain.mapper;

import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.domain.enums.PaymentStatus;
import it.bank.bankcore.payment.domain.model.Payment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class PaymentDomainMapper {

    private static final String DEPOSIT_REASON = "Deposit";
    private static final String WITHDRAW_REASON = "Withdraw";
    private static final String TRANSFER_REASON = "Transfer";

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

    public Payment toDomain(WithdrawCommand command, String targetCurrency) {
        return Payment.builder()
                .sourceAccountUuid(null)
                .targetAccountUuid(command.accountUuid())
                .amount(command.amount())
                .currency(targetCurrency)
                .reason(WITHDRAW_REASON)
                .status(PaymentStatus.PENDING)
                .build();
    }

    public Payment toDomain(TransferCommand command, String targetCurrency) {
        return Payment.builder()
                .sourceAccountUuid(command.sourceAccountUuid())
                .targetAccountUuid(command.targetAccountUuid())
                .amount(command.amount())
                .currency(targetCurrency)
                .reason(ObjectUtils.isEmpty(command.reason()) ? TRANSFER_REASON : command.reason())
                .status(PaymentStatus.PENDING)
                .build();
    }
}

