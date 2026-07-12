package it.bank.bankcore.payment.api.mapper;

import it.bank.bankcore.payment.api.request.ReversalRequest;
import it.bank.bankcore.payment.api.response.ReversalResponse;
import it.bank.bankcore.payment.application.command.ReversalCommand;
import it.bank.bankcore.payment.application.result.ReversalResult;
import it.bank.bankcore.shared.api.ApiInMapper;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class ReversalMapper implements
        ApiInMapper<ReversalRequest, ReversalCommand>,
        ApiOutMapper<ReversalResponse, ReversalResult>
{

    @Override
    public ReversalCommand toCommand(ReversalRequest request) {
        return new ReversalCommand(
                request.paymentId(), request.reason(), request.requestCode()
        );
    }

    @Override
    public ReversalResponse toResponse(ReversalResult result) {
        return new ReversalResponse(
                result.paymentId(),
                result.reversedPaymentId(),
                result.amount(),
                result.currency(),
                result.created()
        );
    }
}
