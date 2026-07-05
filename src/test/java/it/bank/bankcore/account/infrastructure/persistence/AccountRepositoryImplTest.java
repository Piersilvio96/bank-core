package it.bank.bankcore.account.infrastructure.persistence;

import it.bank.bankcore.account.domain.criteria.CreateAccountCriteria;
import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AccountRepositoryImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void saveAndFindByUuid_shouldPersistAndReturnDomainModel() {
        var account = sampleAccount("RSSMRA80A01H501U", "mario.rossi@example.com");

        var saved = accountRepository.save(account);

        assertNotNull(saved.getId());
        assertNotNull(saved.getUuid());

        var found = accountRepository.findByUuid(saved.getUuid()).orElseThrow();
        assertEquals("Mario", found.getFirstName());
        assertEquals("Rossi", found.getLastName());
        assertEquals("EUR", found.getCurrency());
        assertEquals(AccountStatus.ACTIVE, found.getStatus());
    }

    @Test
    void existsMethods_shouldMatchFiscalCodeOrEmail() {
        var saved = accountRepository.save(sampleAccount("ABCDEF12G34H567I", "uno@example.com"));

        assertTrue(accountRepository.existsByFiscalCodeOrEmail("ABCDEF12G34H567I", "other@example.com"));
        assertTrue(accountRepository.existsByFiscalCodeOrEmail("OTHERFC99", "uno@example.com"));

        var criteria = new CreateAccountCriteria(saved.getFiscalCode(), "another@example.com");
        assertTrue(accountRepository.exists(criteria));
    }

    private Account sampleAccount(String fiscalCode, String email) {
        return Account.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .email(email)
                .fiscalCode(fiscalCode)
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }
}


