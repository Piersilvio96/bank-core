package it.bank.bankcore.account.infrastructure.persistence.mapper;

import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.shared.infrastructure.JpaMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountJpaMapper implements JpaMapper<Account, AccountJpaEntity> {

    @Override
    public Account toDomain(AccountJpaEntity entity) {
        return Account.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .fiscalCode(entity.getFiscalCode())
                .email(entity.getEmail())
                .balance(entity.getBalance())
                .city(entity.getCity())
                .country(entity.getCountry())
                .state(entity.getState())
                .phoneNumber(entity.getPhoneNumber())
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public AccountJpaEntity toEntity(Account domain) {
        return AccountJpaEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .fiscalCode(domain.getFiscalCode())
                .email(domain.getEmail())
                .balance(domain.getBalance())
                .city(domain.getCity())
                .country(domain.getCountry())
                .state(domain.getState())
                .phoneNumber(domain.getPhoneNumber())
                .lastName(domain.getLastName())
                .firstName(domain.getFirstName())
                .currency(domain.getCurrency())
                .status(domain.getStatus())
                .build();
    }
}
