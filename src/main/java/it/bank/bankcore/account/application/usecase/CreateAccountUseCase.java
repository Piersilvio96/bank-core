package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.command.CreateAccountCommand;
import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.result.CreateAccountResult;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAccountUseCase implements UseCase<CreateAccountCommand, CreateAccountResult> {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public CreateAccountResult execute(CreateAccountCommand command) {
        if (!userRepository.existsByUuid(command.userUuid())) {
            throw new UserNotFoundException(command.userUuid());
        }

        var domainAccount = accountApplicationMapper.toDomain(command);
        var savedDomain = accountRepository.save(domainAccount);

        return accountApplicationMapper.toResult(savedDomain);
    }

}
