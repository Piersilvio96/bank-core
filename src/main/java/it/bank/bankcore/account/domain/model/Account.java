package it.bank.bankcore.account.domain.model;

import it.bank.bankcore.account.domain.enums.AccountStatus;
import it.bank.bankcore.account.domain.exception.AccountCannotBeClosedException;
import it.bank.bankcore.account.domain.exception.AccountNotActiveException;
import it.bank.bankcore.account.domain.exception.InsufficientFundsException;
import it.bank.bankcore.account.domain.exception.InvalidAmountException;
import it.bank.bankcore.shared.domain.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class Account extends Base {

    private String firstName;
    private String lastName;
    private String email;
    private String fiscalCode;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private BigDecimal balance;
    private final String currency;
    private AccountStatus status;

    public void deposit(BigDecimal amount) {
        ensureActive();
        ensurePositiveAmount(amount);

        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        ensureActive();
        ensurePositiveAmount(amount);
        ensureSufficientFunds(amount);

        this.balance = this.balance.subtract(amount);
    }

    public void close() {
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new AccountCannotBeClosedException("Account balance must be zero before closing");
        }

        this.status = AccountStatus.CLOSED;
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Account is not active");
        }
    }

    private void ensurePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    private void ensureSufficientFunds(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
    }

}
