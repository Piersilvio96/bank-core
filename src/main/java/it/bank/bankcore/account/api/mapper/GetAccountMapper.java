package it.bank.bankcore.account.api.mapper;

import it.bank.bankcore.account.api.response.GetAccountResponse;
import it.bank.bankcore.account.application.result.GetAccountResult;
import it.bank.bankcore.shared.api.ApiOutMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAccountMapper implements ApiOutMapper<GetAccountResponse, GetAccountResult> {

    @Override
    public GetAccountResponse toResponse(GetAccountResult result) {
        return new GetAccountResponse(
                result.uuid(),
                result.userUuid(),
                result.balance(),
                result.currency(),
                result.status()
        );
    }

    public List<GetAccountResponse> toResponses(List<GetAccountResult> results) {
        return results.stream().map(this::toResponse).toList();
    }
}
