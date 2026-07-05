package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.api.response.GetAccountBalanceResponse;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaRepository;
import it.bank.bankcore.account.infrastructure.persistence.mapper.AccountJpaMapper;
import it.bank.bankcore.shared.application.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GetBalanceUseCase implements UseCase<String, GetAccountBalanceResponse>
{
    private final AccountJpaRepository accountRepository;
    private final AccountJpaMapper accountJpaMapper;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public GetAccountBalanceResponse execute(String uuid) {
        var accountEntity = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new AccountNotFoundException(uuid));

        var balanceResult = accountApplicationMapper.toGetAccountBalanceResult(accountJpaMapper.toDomain(accountEntity));

        return new GetAccountBalanceResponse(balanceResult.balance(), balanceResult.currency());
    }
}
