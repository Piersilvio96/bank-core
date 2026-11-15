package it.bank.bankcore.user.application.usecase;

import it.bank.bankcore.shared.application.UseCase;
import it.bank.bankcore.user.application.command.CreateUserCommand;
import it.bank.bankcore.user.application.mapper.UserApplicationMapper;
import it.bank.bankcore.user.application.result.UserResult;
import it.bank.bankcore.user.domain.exception.UserAlreadyExistsException;
import it.bank.bankcore.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserUseCase implements UseCase<CreateUserCommand, UserResult> {
    private final UserRepository userRepository;
    private final UserApplicationMapper userApplicationMapper;

    @Override
    public UserResult execute(CreateUserCommand command) {
        if (userRepository.existsByFiscalCodeOrEmail(command.fiscalCode(), command.email())) {
            throw new UserAlreadyExistsException(command.fiscalCode(), command.email());
        }

        var savedUser = userRepository.save(userApplicationMapper.toDomain(command));
        return userApplicationMapper.toResult(savedUser);
    }
}
