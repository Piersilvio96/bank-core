package it.bank.bankcore.integration;

import it.bank.bankcore.account.api.controller.AccountController;
import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.account.domain.exception.InsufficientFundsException;
import it.bank.bankcore.account.domain.exception.InvalidAmountException;
import it.bank.bankcore.payment.api.controller.PaymentController;
import it.bank.bankcore.payment.api.request.DepositRequest;
import it.bank.bankcore.payment.api.request.TransferRequest;
import it.bank.bankcore.payment.api.request.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class BankFlowIntegrationTest {

    @Autowired
    private AccountController accountController;

    @Autowired
    private PaymentController paymentController;

    @Test
    void shouldCreateAccountDepositAndReadUpdatedBalance() {
        var createResponse = createAccount("mario");

        assertFalse(createResponse.uuid().isBlank());

        var depositResponse = deposit(createResponse.uuid(), new BigDecimal("25.00"));

        assertFalse(depositResponse.paymentId().isBlank());
        assertEquals("EUR", depositResponse.currency());

        assertBalance(createResponse.uuid(), new BigDecimal("25.00"));
    }

    @Test
    void shouldDepositThenWithdrawAndReadUpdatedBalance() {
        var createResponse = createAccount("luigi");

        deposit(createResponse.uuid(), new BigDecimal("40.00"));

        var withdrawResponse = withdraw(createResponse.uuid(), new BigDecimal("15.00"));

        assertFalse(withdrawResponse.paymentId().isBlank());
        assertEquals("EUR", withdrawResponse.currency());

        assertBalance(createResponse.uuid(), new BigDecimal("25.00"));
    }

    @Test
    void shouldTransferBetweenTwoAccountsAndUpdateBothBalances() {
        var source = createAccount("anna-source");
        var target = createAccount("anna-target");

        deposit(source.uuid(), new BigDecimal("70.00"));

        var transferResponse = transfer(source.uuid(), target.uuid(), new BigDecimal("30.00"), "rent");

        assertFalse(transferResponse.paymentId().isBlank());
        assertEquals("EUR", transferResponse.currency());

        assertBalance(source.uuid(), new BigDecimal("40.00"));
        assertBalance(target.uuid(), new BigDecimal("30.00"));
    }

    @Test
    void shouldApplyTwoDepositsWithDifferentRequestCodes() {
        var account = createAccount("double-deposit");

        deposit(account.uuid(), new BigDecimal("25.00"), requestCode());
        deposit(account.uuid(), new BigDecimal("25.00"), requestCode());

        assertBalance(account.uuid(), new BigDecimal("50.00"));
    }

    @Test
    void shouldRejectWithdrawWhenFundsAreInsufficient() {
        var account = createAccount("insufficient-withdraw");
        deposit(account.uuid(), new BigDecimal("20.00"));

        assertThrows(InsufficientFundsException.class,
                () -> withdraw(account.uuid(), new BigDecimal("50.00")));

        assertBalance(account.uuid(), new BigDecimal("20.00"));
    }

    @Test
    void shouldRejectDepositWithZeroAmount() {
        var account = createAccount("zero-deposit");

        assertThrows(InvalidAmountException.class,
                () -> deposit(account.uuid(), BigDecimal.ZERO));

        assertBalance(account.uuid(), BigDecimal.ZERO);
    }

    @Test
    void shouldRejectTransferWhenSourceFundsAreInsufficient() {
        var source = createAccount("insufficient-transfer-source");
        var target = createAccount("insufficient-transfer-target");
        deposit(source.uuid(), new BigDecimal("10.00"));
        deposit(target.uuid(), new BigDecimal("5.00"));

        assertThrows(InsufficientFundsException.class,
                () -> transfer(source.uuid(), target.uuid(), new BigDecimal("30.00"), "overspend"));

        assertBalance(source.uuid(), new BigDecimal("10.00"));
        assertBalance(target.uuid(), new BigDecimal("5.00"));
    }

    @Test
    void shouldHandleMultiStepFlowAndKeepBalancesConsistent() {
        var accountA = createAccount("flow-a");
        var accountB = createAccount("flow-b");
        var accountC = createAccount("flow-c");

        deposit(accountA.uuid(), new BigDecimal("100.00"));
        transfer(accountA.uuid(), accountB.uuid(), new BigDecimal("40.00"), "rent");
        transfer(accountB.uuid(), accountC.uuid(), new BigDecimal("20.00"), "split");
        withdraw(accountA.uuid(), new BigDecimal("30.00"));

        assertBalance(accountA.uuid(), new BigDecimal("30.00"));
        assertBalance(accountB.uuid(), new BigDecimal("20.00"));
        assertBalance(accountC.uuid(), new BigDecimal("20.00"));
    }

    private it.bank.bankcore.payment.api.response.DepositResponse deposit(String accountUuid, BigDecimal amount) {
        return deposit(accountUuid, amount, requestCode());
    }

    private it.bank.bankcore.payment.api.response.DepositResponse deposit(String accountUuid, BigDecimal amount, String requestCode) {
        return paymentController.deposit(new DepositRequest(accountUuid, amount, "EUR", requestCode)).getBody();
    }

    private it.bank.bankcore.payment.api.response.WithdrawResponse withdraw(String accountUuid, BigDecimal amount) {
        return paymentController.withdraw(new WithdrawRequest(accountUuid, amount, "EUR", requestCode())).getBody();
    }

    private it.bank.bankcore.payment.api.response.TransferResponse transfer(String sourceAccountUuid,
                                                                           String targetAccountUuid,
                                                                           BigDecimal amount,
                                                                           String reason) {
        return paymentController.transfer(new TransferRequest(
                sourceAccountUuid,
                targetAccountUuid,
                amount,
                "EUR",
                reason,
                requestCode()
        )).getBody();
    }

    private void assertBalance(String accountUuid, BigDecimal expectedBalance) {
        var balanceResponse = accountController.getAccountBalance(accountUuid).getBody();
        assertEquals(0, balanceResponse.balance().compareTo(expectedBalance));
        assertEquals("EUR", balanceResponse.currency());
    }

    private String requestCode() {
        return UUID.randomUUID().toString();
    }

    private it.bank.bankcore.account.api.response.CreateAccountResponse createAccount(String userPrefix) {
        var suffix = requestCode().substring(0, 8);

        return accountController.createAccount(new CreateAccountRequest(
                "Mario",
                "Rossi",
                userPrefix + "." + suffix + "@example.com",
                "+393331234567",
                "RSS" + suffix.toUpperCase(),
                "Rome",
                "RM",
                "Italy",
                "EUR"
        )).getBody();
    }
}

