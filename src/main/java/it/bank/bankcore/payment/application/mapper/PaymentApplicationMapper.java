package it.bank.bankcore.payment.application.mapper;

import it.bank.bankcore.payment.application.result.DepositResult;
import it.bank.bankcore.payment.application.result.TransferResult;
import it.bank.bankcore.payment.application.result.WithdrawResult;
import it.bank.bankcore.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentApplicationMapper {


    public DepositResult toDepositResult(Payment payment, boolean created) {
        return new DepositResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency(),
                created
        );
    }

    public WithdrawResult toWithdrawResult(Payment payment, boolean created) {
        return new WithdrawResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency(),
                created
        );
    }

    public TransferResult toTransferResult(Payment payment, boolean created) {
        return new TransferResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency(),
                created
        );
    }
}
