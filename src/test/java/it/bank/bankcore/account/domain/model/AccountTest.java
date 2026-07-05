package it.bank.bankcore.account.domain.model;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountCannotBeClosedException;
import it.bank.bankcore.account.domain.exception.AccountNotActiveException;
import it.bank.bankcore.account.domain.exception.InsufficientFundsException;
import it.bank.bankcore.account.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    @Test
    void deposit_shouldIncreaseBalance() {
        var account = activeAccount(new BigDecimal("100.00"));

        account.deposit(new BigDecimal("50.00"));

        assertEquals(0, account.getBalance().compareTo(new BigDecimal("150.00")));
    }

    @Test
    void deposit_shouldThrowWhenAccountNotActive() {
        var account = closedAccount(new BigDecimal("100.00"));

        assertThrows(AccountNotActiveException.class, () -> account.deposit(new BigDecimal("10.00")));
    }

    @Test
    void deposit_shouldThrowWhenAmountIsZeroOrNegative() {
        var account = activeAccount(new BigDecimal("100.00"));

        assertThrows(InvalidAmountException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(InvalidAmountException.class, () -> account.deposit(new BigDecimal("-1.00")));
    }

    @Test
    void withdraw_shouldDecreaseBalance() {
        var account = activeAccount(new BigDecimal("100.00"));

        account.withdraw(new BigDecimal("40.00"));

        assertEquals(0, account.getBalance().compareTo(new BigDecimal("60.00")));
    }

    @Test
    void withdraw_shouldThrowWhenInsufficientFunds() {
        var account = activeAccount(new BigDecimal("20.00"));

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("30.00")));
    }

    @Test
    void withdraw_shouldThrowWhenAccountNotActive() {
        var account = closedAccount(new BigDecimal("100.00"));

        assertThrows(AccountNotActiveException.class, () -> account.withdraw(new BigDecimal("10.00")));
    }

    @Test
    void withdraw_shouldThrowWhenAmountIsZeroOrNegative() {
        var account = activeAccount(new BigDecimal("100.00"));

        assertThrows(InvalidAmountException.class, () -> account.withdraw(BigDecimal.ZERO));
        assertThrows(InvalidAmountException.class, () -> account.withdraw(new BigDecimal("-1.00")));
    }

    @Test
    void close_shouldSetStatusClosedWhenBalanceIsZero() {
        var account = activeAccount(BigDecimal.ZERO);

        account.close();

        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    void close_shouldThrowWhenBalanceIsPositive() {
        var account = activeAccount(new BigDecimal("1.00"));

        assertThrows(AccountCannotBeClosedException.class, account::close);
    }

    private Account activeAccount(BigDecimal balance) {
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
                .balance(balance)
                .currency("EUR")
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private Account closedAccount(BigDecimal balance) {
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
                .balance(balance)
                .currency("EUR")
                .status(AccountStatus.CLOSED)
                .build();
    }
}

