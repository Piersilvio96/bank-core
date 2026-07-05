package it.bank.bankcore.account.api.mapper;

import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.result.CreateAccountResult;
import it.bank.bankcore.shared.api.ApiInMapper;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountMapper implements
        ApiInMapper<CreateAccountRequest, CreateAccountCommand>,
        ApiOutMapper<CreateAccountResponse, CreateAccountResult>
{

    @Override
    public CreateAccountCommand toCommand(CreateAccountRequest request) {
        return new CreateAccountCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.fiscalCode(),
                request.city(),
                request.state(),
                request.country(),
                request.currency()
        );
    }

    @Override
    public CreateAccountResponse toResponse(CreateAccountResult result) {
        return new CreateAccountResponse(
                result.uuid(),
                result.firstName(),
                result.lastName(),
                result.email(),
                result.phoneNumber(),
                result.fiscalCode(),
                result.city(),
                result.state(),
                result.country(),
                result.balance(),
                result.currency()
        );
    }
}
