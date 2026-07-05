package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.query.GetAccountQuery;
import it.bank.bankcore.account.application.result.GetAccountResult;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GetAccountUseCase implements UseCase<GetAccountQuery, GetAccountResult>
{
    private final AccountRepository accountRepository;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public GetAccountResult execute(GetAccountQuery query) {
        var account = accountRepository.findByUuid(query.uuid())
                .orElseThrow(() -> new AccountNotFoundException(query.uuid()));

        return accountApplicationMapper.toGetAccountResult(account);
    }
}
