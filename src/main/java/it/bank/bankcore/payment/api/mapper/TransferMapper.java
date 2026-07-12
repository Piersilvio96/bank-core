package it.bank.bankcore.payment.api.mapper;

import it.bank.bankcore.payment.api.request.TransferRequest;
import it.bank.bankcore.payment.api.response.TransferResponse;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.application.result.TransferResult;
import it.bank.bankcore.shared.api.ApiInMapper;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper implements
        ApiInMapper<TransferRequest, TransferCommand>,
        ApiOutMapper<TransferResponse, TransferResult> {

    @Override
    public TransferCommand toCommand(TransferRequest request) {
        return new TransferCommand(request.sourceAccountUuid(), request.targetAccountUuid(), request.amount(), request.currency(), request.reason(), request.requestCode());
    }

    @Override
    public TransferResponse toResponse(TransferResult result) {
        return new TransferResponse(result.paymentId(), result.amount(), result.currency(), result.created());
    }
}
