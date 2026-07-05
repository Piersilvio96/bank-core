package it.bank.bankcore.account.infrastructure.persistence.mapper;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountJpaMapperTest {

    private final AccountJpaMapper mapper = new AccountJpaMapper();

    @Test
    void toDomain_shouldMapEntityFields() {
        var entity = AccountJpaEntity.builder()
                .id(1L)
                .uuid("acc-uuid")
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@example.com")
                .fiscalCode("RSSMRA80A01H501U")
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .balance(new BigDecimal("100.00"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();

        var domain = mapper.toDomain(entity);

        assertEquals(entity.getUuid(), domain.getUuid());
        assertEquals(entity.getEmail(), domain.getEmail());
        assertEquals(0, entity.getBalance().compareTo(domain.getBalance()));
    }

    @Test
    void toEntity_shouldMapDomainFields() {
        var domain = Account.builder()
                .id(1L)
                .uuid("acc-uuid")
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@example.com")
                .fiscalCode("RSSMRA80A01H501U")
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .balance(new BigDecimal("100.00"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();

        var entity = mapper.toEntity(domain);

        assertEquals(domain.getUuid(), entity.getUuid());
        assertEquals(domain.getEmail(), entity.getEmail());
        assertEquals(0, domain.getBalance().compareTo(entity.getBalance()));
    }
}

