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


    public DepositResult toDepositResult(Payment payment) {
        return new DepositResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency()
        );
    }

    public WithdrawResult toWithdrawResult(Payment payment) {
        return new WithdrawResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency()
        );
    }

    public TransferResult toTransferResult(Payment payment) {
        return new TransferResult(
                payment.getUuid(),
                payment.getAmount(),
                payment.getCurrency()
        );
    }
}

