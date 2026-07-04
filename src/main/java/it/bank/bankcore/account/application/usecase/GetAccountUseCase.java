package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.api.response.GetAccountResponse;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GetAccountUseCase implements UseCase<String, GetAccountResponse>
{
    private final AccountRepository accountRepository;

    @Override
    public GetAccountResponse execute(String uuid) {
        // For example, you can call a repository to get the account details
        var account = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new AccountNotFoundException(uuid));
        // and return a GetAccountResponse object.
        return GetAccountResponse.builder()
                .uuid(account.getUuid())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .fiscalCode(account.getFiscalCode())
                .phoneNumber(account.getPhoneNumber())
                .city(account.getCity())
                .state(account.getState())
                .country(account.getCountry())
                .status(account.getStatus())
                .build();
    }
}
