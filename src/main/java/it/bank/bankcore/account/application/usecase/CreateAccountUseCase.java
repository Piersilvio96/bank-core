package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.result.CreateAccountResult;
import it.bank.bankcore.account.domain.exception.AccountAlreadyExistsException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAccountUseCase implements UseCase<CreateAccountCommand, CreateAccountResult> {

    private final AccountRepository accountRepository;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public CreateAccountResult execute(CreateAccountCommand command) {

        boolean exists = accountRepository.existsByFiscalCodeOrEmail(command.fiscalCode(), command.email());

        if (exists) {
            throw new AccountAlreadyExistsException(command.fiscalCode(), command.email());
        }

        var domainAccount = accountApplicationMapper.toDomain(command);
        var savedDomain = accountRepository.save(domainAccount);
        return accountApplicationMapper.toResult(savedDomain);
    }
    
}
