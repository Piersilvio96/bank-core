package it.bank.bankcore.payment.application.validation;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountNotFoundException;
import it.bank.bankcore.account.domain.exception.AccountStatusException;
import it.bank.bankcore.account.domain.model.Account;
import it.bank.bankcore.account.domain.repository.AccountRepository;
import it.bank.bankcore.payment.application.command.WithdrawCommand;
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
class WithdrawValidationRuleTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private WithdrawValidationRule validationRule;

    @Test
    void validate_shouldPassForActiveAccountAndMatchingCurrency() {
        var command = new WithdrawCommand("acc-uuid", new BigDecimal("10.00"), "EUR");
        when(accountRepository.findByUuid("acc-uuid")).thenReturn(Optional.of(sampleAccount(AccountStatus.ACTIVE, "EUR")));

        assertDoesNotThrow(() -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenAccountMissing() {
        var command = new WithdrawCommand("missing", new BigDecimal("10.00"), "EUR");
        when(accountRepository.findByUuid("missing")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenAccountNotActive() {
        var command = new WithdrawCommand("acc-uuid", new BigDecimal("10.00"), "EUR");
        when(accountRepository.findByUuid("acc-uuid")).thenReturn(Optional.of(sampleAccount(AccountStatus.CLOSED, "EUR")));

        assertThrows(AccountStatusException.class, () -> validationRule.validate(command));
    }

    @Test
    void validate_shouldThrowWhenCurrencyDiffers() {
        var command = new WithdrawCommand("acc-uuid", new BigDecimal("10.00"), "USD");
        when(accountRepository.findByUuid("acc-uuid")).thenReturn(Optional.of(sampleAccount(AccountStatus.ACTIVE, "EUR")));

        assertThrows(CurrencyAccountException.class, () -> validationRule.validate(command));
    }

    private Account sampleAccount(AccountStatus status, String currency) {
        return Account.builder()
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
                .currency(currency)
                .status(status)
                .build();
    }
}

