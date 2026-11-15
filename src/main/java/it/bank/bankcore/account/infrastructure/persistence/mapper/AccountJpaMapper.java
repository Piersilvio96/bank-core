package it.bank.bankcore.account.infrastructure.persistence.mapper;

import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.shared.infrastructure.JpaMapper;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountJpaMapper implements JpaMapper<Account, AccountJpaEntity> {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Account toDomain(AccountJpaEntity entity) {
        return Account.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .userUuid(entity.getUser() != null ? entity.getUser().getUuid() : null)
                .balance(entity.getBalance())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public AccountJpaEntity toEntity(Account domain) {
        var user = userJpaRepository.findByUuid(domain.getUserUuid())
                .orElseThrow(() -> new UserNotFoundException(domain.getUserUuid()));

        return AccountJpaEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .user(user)
                .balance(domain.getBalance())
                .currency(domain.getCurrency())
                .status(domain.getStatus())
                .build();
    }
}
