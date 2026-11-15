package it.bank.bankcore.user.application.usecase;

import it.bank.bankcore.shared.application.UseCase;
import it.bank.bankcore.user.application.mapper.UserApplicationMapper;
import it.bank.bankcore.user.application.query.GetUserQuery;
import it.bank.bankcore.user.application.result.UserResult;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserUseCase implements UseCase<GetUserQuery, UserResult> {
    private final UserRepository userRepository;
    private final UserApplicationMapper userApplicationMapper;

    @Override
    public UserResult execute(GetUserQuery query) {
        var user = userRepository.findByUuid(query.uuid())
                .orElseThrow(() -> new UserNotFoundException(query.uuid()));
        return userApplicationMapper.toResult(user);
    }
}
