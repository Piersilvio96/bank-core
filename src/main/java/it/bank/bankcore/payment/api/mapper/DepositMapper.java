package it.bank.bankcore.payment.api.mapper;

import it.bank.bankcore.payment.api.request.DepositRequest;
import it.bank.bankcore.payment.application.command.DepositCommand;
import it.bank.bankcore.payment.api.response.DepositResponse;
import it.bank.bankcore.payment.application.result.DepositResult;
import it.bank.bankcore.shared.api.ApiInMapper;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class DepositMapper implements
        ApiInMapper<DepositRequest, DepositCommand>,
        ApiOutMapper<DepositResponse, DepositResult> {

    @Override
    public DepositCommand toCommand(DepositRequest request) {
        return new DepositCommand(request.accountUuid(), request.amount(), request.currency(), request.requestCode());
    }

    @Override
    public DepositResponse toResponse(DepositResult result) {
        return new DepositResponse(result.paymentId(), result.amount(), result.currency());
    }
}

