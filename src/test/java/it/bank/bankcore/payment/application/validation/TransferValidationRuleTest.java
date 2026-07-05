package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.exception.AccountStatusException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.payment.application.command.TransferCommand;
import it.bank.bankcore.payment.domain.exception.CurrencyAccountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferValidationRuleTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferValidationRule validationRule;

    @Test
    void validate_shouldPassForTwoActiveAccountsAndMatchingCurrency() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sampleAccount("source-uuid", AccountStatus.ACTIVE, "EUR")));
        when(accountRepository.findByUuid("target-uuid")).thenReturn(Optional.of(sampleAccount("target-uuid", AccountStatus.ACTIVE, "EUR")));

        assertDoesNotThrow(() -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenSourceMissing() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenTargetMissing() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sampleAccount("source-uuid", AccountStatus.ACTIVE, "EUR")));
        when(accountRepository.findByUuid("target-uuid")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenSourceNotActive() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sampleAccount("source-uuid", AccountStatus.CLOSED, "EUR")));
        when(accountRepository.findByUuid("target-uuid")).thenReturn(Optional.of(sampleAccount("target-uuid", AccountStatus.ACTIVE, "EUR")));

        assertThrows(AccountStatusException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenTargetNotActive() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sampleAccount("source-uuid", AccountStatus.ACTIVE, "EUR")));
        when(accountRepository.findByUuid("target-uuid")).thenReturn(Optional.of(sampleAccount("target-uuid", AccountStatus.CLOSED, "EUR")));

        assertThrows(AccountStatusException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenCurrencyDiffersFromTarget() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "USD", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sampleAccount("source-uuid", AccountStatus.ACTIVE, "USD")));
        when(accountRepository.findByUuid("target-uuid")).thenReturn(Optional.of(sampleAccount("target-uuid", AccountStatus.ACTIVE, "EUR")));

        assertThrows(CurrencyAccountException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenCurrencyDiffersFromSource() {
        var command = new TransferCommand("source-uuid", "target-uuid", new BigDecimal("10.00"), "EUR", "rent");
        when(accountRepository.findByUuid("source-uuid")).thenReturn(Optional.of(sampleAccount("source-uuid", AccountStatus.ACTIVE, "USD")));
        when(accountRepository.findByUuid("target-uuid")).thenReturn(Optional.of(sampleAccount("target-uuid", AccountStatus.ACTIVE, "EUR")));

        assertThrows(CurrencyAccountException.class, () -> validationRule.validate(command));
    }

    private Account sampleAccount(String uuid, AccountStatus status, String currency) {
        return Account.builder()
                .uuid(uuid)
                .firstName("Mario")
                .lastName("Rossi")
                .email(uuid + "@example.com")
                .fiscalCode("RSSMRA80A01H501U")
                .phoneNumber("+393331234567")
                .city("Rome")
                .state("RM")
                .country("Italy")
                .balance(new BigDecimal("100.00"))
                .currency(currency)
                .status(status)
                .build();
    }
}

