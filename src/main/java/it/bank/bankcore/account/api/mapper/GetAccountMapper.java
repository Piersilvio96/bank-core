package it.bank.bankcore.account.api.mapper;

import it.bank.bankcore.account.api.response.GetAccountResponse;
import it.bank.bankcore.account.application.result.GetAccountResult;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class GetAccountMapper implements ApiOutMapper<GetAccountResponse, GetAccountResult> {

    @Override
    public GetAccountResponse toResponse(GetAccountResult result) {
        return new GetAccountResponse(
                result.uuid(),
                result.firstName(),
                result.lastName(),
                result.email(),
                result.fiscalCode(),
                result.phoneNumber(),
                result.city(),
                result.state(),
                result.country(),
                result.status()
        );
    }
}

