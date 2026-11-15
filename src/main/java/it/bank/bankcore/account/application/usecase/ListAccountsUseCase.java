package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.query.ListAccountsQuery;
import it.bank.bankcore.account.application.result.GetAccountResult;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAccountsUseCase implements UseCase<ListAccountsQuery, List<GetAccountResult>> {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public List<GetAccountResult> execute(ListAccountsQuery query) {
        if (!userRepository.existsByUuid(query.userUuid())) {
            throw new UserNotFoundException(query.userUuid());
        }

        return accountRepository.findAllByUserUuid(query.userUuid()).stream()
                .map(accountApplicationMapper::toGetAccountResult)
                .toList();
    }
}
