package it.bank.bankcore.account.api.mapper;

import it.bank.bankcore.account.api.response.GetAccountBalanceResponse;
import it.bank.bankcore.account.application.result.GetAccountBalanceResult;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

@Component
public class GetBalanceMapper implements ApiOutMapper<GetAccountBalanceResponse, GetAccountBalanceResult> {

    @Override
    public GetAccountBalanceResponse toResponse(GetAccountBalanceResult result) {
        return new GetAccountBalanceResponse(result.balance(), result.currency());
    }
}

