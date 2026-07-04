package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.domain.model.AccountEntity;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.account.domain.specification.CreateAccountCriteriaSpecification;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAccountUseCase implements UseCase<CreateAccountRequest, CreateAccountResponse> {

    private final AccountRepository accountRepository;

    @Override
    public CreateAccountResponse execute(CreateAccountRequest input) {
        // Implement the account creation logic here
        boolean exists = accountRepository.exists(
                CreateAccountCriteriaSpecification.createSpecification(
                        new CreateAccountCriteria(input.fiscalCode(), input.email())
                )
        );
        if (exists) {
            throw new IllegalArgumentException("Account with the same fiscal code or email already exists.");
        }
        // For example, you can create an account entity, save it to the database, and return a response
        AccountEntity account = AccountEntity.builder()
                .firstName(input.firstName())
                .lastName(input.lastName())
                .email(input.email())
                .fiscalCode(input.fiscalCode())
                .phoneNumber(input.phoneNumber())
                .city(input.city())
                .state(input.state())
                .country(input.country())
                .currency(input.currency() != null ? input.currency() : "EUR")
                .build();
        accountRepository.save(account);

        CreateAccountResponse response = CreateAccountResponse.builder()
                .uuid(account.getUuid())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .fiscalCode(account.getFiscalCode())
                .phoneNumber(account.getPhoneNumber())
                .city(account.getCity())
                .state(account.getState())
                .country(account.getCountry())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
        return response;
    }
    
}
