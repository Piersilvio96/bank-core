package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.domain.model.AccountEntity;
import it.bank.bankcore.account.domain.specification.CreateAccountCriteriaSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.shared.application.UseCase;
import it.bank.bankcore.account.domain.repository.AccountRepository;

@Component
@AllArgsConstructor
@Transactional
public class CreateAccountUseCase implements UseCase<CreateAccountRequest, CreateAccountResponse> {

    private final AccountRepository accountRepository;

    @Override
    public CreateAccountResponse execute(CreateAccountRequest input) {
        // Implement the account creation logic here
        boolean exists = accountRepository.exists(
                CreateAccountCriteriaSpecification.createSpecification(
                        new CreateAccountCriteria(input.getFiscalCode(), input.getEmail())
                )
        );
        if (exists) {
            throw new IllegalArgumentException("Account with the same fiscal code or email already exists.");
        }
        // For example, you can create an account entity, save it to the database, and return a response
        AccountEntity account = AccountEntity.builder()
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .email(input.getEmail())
                .fiscalCode(input.getFiscalCode())
                .phoneNumber(input.getPhoneNumber())
                .city(input.getCity())
                .state(input.getState())
                .country(input.getCountry())
                .currency(input.getCurrency())
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
