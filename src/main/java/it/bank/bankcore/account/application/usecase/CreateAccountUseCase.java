package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.api.mapper.CreateAccountMapper;
import it.bank.bankcore.account.api.response.CreateAccountResponse;
import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaRepository;
import it.bank.bankcore.account.infrastructure.persistence.mapper.AccountJpaMapper;
import it.bank.bankcore.account.infrastructure.persistence.specification.CreateAccountCriteriaSpecification;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAccountUseCase implements UseCase<CreateAccountCommand, CreateAccountResponse> {

    private final AccountJpaRepository accountRepository;
    private final AccountApplicationMapper accountApplicationMapper;
    private final CreateAccountMapper createAccountMapper;
    private final AccountJpaMapper accountJpaMapper;

    @Override
    public CreateAccountResponse execute(CreateAccountCommand command) {

        boolean exists = accountRepository.exists(
                CreateAccountCriteriaSpecification.createSpecification(
                        new CreateAccountCriteria(command.fiscalCode(), command.email())
                )
        );

        if (exists) {
            throw new IllegalArgumentException("Account with the same fiscal code or email already exists.");
        }

        var domainAccount = accountApplicationMapper.toDomain(command);
        var savedEntity = accountRepository.save(accountJpaMapper.toEntity(domainAccount));
        var savedDomain = accountJpaMapper.toDomain(savedEntity);
        var result = accountApplicationMapper.toResult(savedDomain);

        return createAccountMapper.toResponse(result);
    }
    
}
