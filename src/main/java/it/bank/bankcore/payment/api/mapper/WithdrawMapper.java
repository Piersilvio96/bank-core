package it.bank.bankcore.payment.api.mapper;

import it.bank.bankcore.payment.api.request.WithdrawRequest;
import it.bank.bankcore.payment.api.response.WithdrawResponse;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
import it.bank.bankcore.payment.application.result.WithdrawResult;
import it.bank.bankcore.shared.api.ApiInMapper;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class WithdrawMapper implements
        ApiInMapper<WithdrawRequest, WithdrawCommand>,
        ApiOutMapper<WithdrawResponse, WithdrawResult> {

    @Override
    public WithdrawCommand toCommand(WithdrawRequest request) {
        return new WithdrawCommand(request.accountUuid(), request.amount(), request.currency());
    }

    @Override
    public WithdrawResponse toResponse(WithdrawResult result) {
        return new WithdrawResponse(result.paymentId(), result.amount(), result.currency());
    }
}

