package it.bank.bankcore.account.infrastructure.persistence;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.user.domain.enums.UserStatus;
import it.bank.bankcore.user.domain.exception.UserNotFoundException;
import it.bank.bankcore.user.domain.model.User;
import it.bank.bankcore.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AccountRepositoryImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByUuid_shouldPersistAndReturnDomainModel() {
        var user = saveUser();

        var saved = accountRepository.save(sampleAccount(user.getUuid()));

        assertNotNull(saved.getId());
        assertNotNull(saved.getUuid());

        var found = accountRepository.findByUuid(saved.getUuid()).orElseThrow();
        assertEquals(user.getUuid(), found.getUserUuid());
        assertEquals("EUR", found.getCurrency());
        assertEquals(AccountStatus.ACTIVE, found.getStatus());
    }

    @Test
    void findAllByUserUuid_shouldReturnOnlyAccountsOwnedByUser() {
        var owner = saveUser();
        var other = saveUser();

        var first = accountRepository.save(sampleAccount(owner.getUuid()));
        var second = accountRepository.save(sampleAccount(owner.getUuid()));
        accountRepository.save(sampleAccount(other.getUuid()));

        var accounts = accountRepository.findAllByUserUuid(owner.getUuid());

        assertEquals(2, accounts.size());
        assertEquals(first.getUuid(), accounts.get(0).getUuid());
        assertEquals(second.getUuid(), accounts.get(1).getUuid());
        assertTrue(accounts.stream().allMatch(account -> owner.getUuid().equals(account.getUserUuid())));
    }

    @Test
    void save_shouldFailWhenUserDoesNotExist() {
        // Spring translates the EntityNotFoundException raised inside the @Repository.
        var exception = assertThrows(
                JpaObjectRetrievalFailureException.class,
                () -> accountRepository.save(sampleAccount("missing-user"))
        );

        assertInstanceOf(UserNotFoundException.class, exception.getCause());
    }

    private User saveUser() {
        var unique = UUID.randomUUID().toString();
        return userRepository.save(User.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .email(unique + "@example.com")
                .fiscalCode(unique)
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .status(UserStatus.ACTIVE)
                .build());
    }

    private Account sampleAccount(String userUuid) {
        return Account.builder()
                .userUuid(userUuid)
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
