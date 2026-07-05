package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.application.query.GetBalanceQuery;
import it.bank.bankcore.account.application.result.GetAccountBalanceResult;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GetBalanceUseCase implements UseCase<GetBalanceQuery, GetAccountBalanceResult>
{
    private final AccountRepository accountRepository;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public GetAccountBalanceResult execute(GetBalanceQuery query) {
        var account = accountRepository.findByUuid(query.uuid())
                .orElseThrow(() -> new AccountNotFoundException(query.uuid()));

        return accountApplicationMapper.toGetAccountBalanceResult(account);
    }
}
