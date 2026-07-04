package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.api.response.GetAccountBalanceResponse;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GetBalanceUseCase implements UseCase<String, GetAccountBalanceResponse>
{
    private final AccountRepository accountRepository;

    @Override
    public GetAccountBalanceResponse execute(String uuid) {
        // Implement the logic to retrieve account details by UUID
        var account = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new AccountNotFoundException(uuid));
        // and return a GetAccountBalanceResponse object.
        return GetAccountBalanceResponse.builder()
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
