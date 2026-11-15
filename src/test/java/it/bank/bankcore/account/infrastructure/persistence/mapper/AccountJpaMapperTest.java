package it.bank.bankcore.account.infrastructure.persistence.mapper;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.infrastructure.persistence.AccountJpaEntity;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.infrastructure.persistence.UserJpaEntity;
import it.bank.bankcore.user.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountJpaMapperTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private AccountJpaMapper mapper;

    @Test
    void toDomain_shouldMapEntityFields() {
        var entity = AccountJpaEntity.builder()
                .id(1L)
                .uuid("acc-uuid")
                .user(UserJpaEntity.builder().uuid("user-uuid").build())
                .balance(new BigDecimal("100.00"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();

        var domain = mapper.toDomain(entity);

        assertEquals(entity.getUuid(), domain.getUuid());
        assertEquals("user-uuid", domain.getUserUuid());
        assertEquals(0, entity.getBalance().compareTo(domain.getBalance()));
    }

    @Test
    void toEntity_shouldMapDomainFieldsAndResolveUser() {
        var user = UserJpaEntity.builder().uuid("user-uuid").build();
        when(userJpaRepository.findByUuid("user-uuid")).thenReturn(Optional.of(user));

        var entity = mapper.toEntity(sampleAccount());

        assertEquals("acc-uuid", entity.getUuid());
        assertSame(user, entity.getUser());
        assertEquals(0, new BigDecimal("100.00").compareTo(entity.getBalance()));
    }

    @Test
    void toEntity_shouldThrowWhenUserMissing() {
        when(userJpaRepository.findByUuid("user-uuid")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> mapper.toEntity(sampleAccount()));
    }

    private Account sampleAccount() {
        return Account.builder()
                .id(1L)
                .uuid("acc-uuid")
                .userUuid("user-uuid")
                .balance(new BigDecimal("100.00"))
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
