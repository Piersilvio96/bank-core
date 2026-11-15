package it.bank.bankcore.account.infrastructure.persistence;

import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.account.infrastructure.persistence.mapper.AccountJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Transactional so that the lazy account -> user relation can be read while mapping to the domain model,
 * even when the repository is called outside a use case transaction.
 */
@Repository
@RequiredArgsConstructor
@Transactional
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountJpaRepository accountJpaRepository;
    private final AccountJpaMapper accountJpaMapper;

    @Override
    public Account save(Account account) {
        var accountJpaEntity = accountJpaMapper.toEntity(account);
        var savedEntity = accountJpaRepository.save(accountJpaEntity);
        return accountJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findByUuid(String uuid) {
        var accountJpaEntity = accountJpaRepository.findByUuid(uuid);
        return accountJpaEntity.map(accountJpaMapper::toDomain);
    }

    @Override
    public Optional<Account> findByUuidForUpdate(String uuid) {
        var accountJpaEntity = accountJpaRepository.findByUuidForUpdate(uuid);
        return accountJpaEntity.map(accountJpaMapper::toDomain);
    }

    @Override
    public List<Account> findAllByUserUuid(String userUuid) {
        return accountJpaRepository.findAllByUserUuid(userUuid).stream()
                .map(accountJpaMapper::toDomain)
                .toList();
    }
}
