package it.bank.bankcore.account.infrastructure.persistence;

import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.account.infrastructure.persistence.mapper.AccountJpaMapper;
import it.bank.bankcore.account.infrastructure.persistence.specification.CreateAccountCriteriaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
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
    public Boolean exists(CreateAccountCriteria criteria) {
        var createAccountSpecification = CreateAccountCriteriaSpecification.createSpecification(criteria);
        return accountJpaRepository.exists(createAccountSpecification);
    }
}
