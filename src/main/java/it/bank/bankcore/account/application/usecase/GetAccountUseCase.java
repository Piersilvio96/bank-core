package it.bank.bankcore.account.application.usecase;

import it.bank.bankcore.account.application.mapper.AccountApplicationMapper;
import it.bank.bankcore.account.api.response.GetAccountResponse;
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
public class GetAccountUseCase implements UseCase<String, GetAccountResponse>
{
    private final AccountJpaRepository accountRepository;
    private final AccountJpaMapper accountJpaMapper;
    private final AccountApplicationMapper accountApplicationMapper;

    @Override
    public GetAccountResponse execute(String uuid) {
        var accountEntity = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new AccountNotFoundException(uuid));

        var accountResult = accountApplicationMapper.toGetAccountResult(accountJpaMapper.toDomain(accountEntity));

        return new GetAccountResponse(
                accountResult.uuid(),
                accountResult.firstName(),
                accountResult.lastName(),
                accountResult.email(),
                accountResult.fiscalCode(),
                accountResult.phoneNumber(),
                accountResult.city(),
                accountResult.state(),
                accountResult.country(),
                accountResult.status()
        );
    }
}
