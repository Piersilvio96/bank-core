package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.api.response.GetAccountBalanceResponse;
import it.bank.bankcore.account.api.response.GetAccountResponse;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.account.domain.specification.GetAccountCriteriaSpecification;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Transactional
public class GetBalanceUseCase implements UseCase<String, GetAccountBalanceResponse>
{
    private final AccountRepository accountRepository;

    @Override
    public GetAccountBalanceResponse execute(String uuid) {
        // Implement the logic to retrieve account details by UUID
        var specification = GetAccountCriteriaSpecification.createSpecification(uuid);
        // For example, you can call a repository to get the account details
        var account = accountRepository.findOne(specification)
                .orElseThrow(AccountNotFoundException::new);
        // and return a GetAccountBalanceResponse object.
        return GetAccountBalanceResponse.builder()
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
