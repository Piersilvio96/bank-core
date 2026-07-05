package it.bank.bankcore.integration;

import it.bank.bankcore.account.api.controller.AccountController;
import it.bank.bankcore.account.api.request.CreateAccountRequest;
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

        var depositResponse = paymentController.deposit(new DepositRequest(
                createResponse.uuid(),
                new BigDecimal("25.00"),
                "EUR"
        ));

        assertFalse(depositResponse.paymentId().isBlank());
        assertEquals("EUR", depositResponse.currency());

        var balanceResponse = accountController.getAccountBalance(createResponse.uuid());
        assertEquals(0, balanceResponse.balance().compareTo(new BigDecimal("25.00")));
        assertEquals("EUR", balanceResponse.currency());
    }

    @Test
    void shouldDepositThenWithdrawAndReadUpdatedBalance() {
        var createResponse = createAccount("luigi");

        paymentController.deposit(new DepositRequest(
                createResponse.uuid(),
                new BigDecimal("40.00"),
                "EUR"
        ));

        var withdrawResponse = paymentController.withdraw(new WithdrawRequest(
                createResponse.uuid(),
                new BigDecimal("15.00"),
                "EUR"
        ));

        assertFalse(withdrawResponse.paymentId().isBlank());
        assertEquals("EUR", withdrawResponse.currency());

        var balanceResponse = accountController.getAccountBalance(createResponse.uuid());
        assertEquals(0, balanceResponse.balance().compareTo(new BigDecimal("25.00")));
    }

    @Test
    void shouldTransferBetweenTwoAccountsAndUpdateBothBalances() {
        var source = createAccount("anna-source");
        var target = createAccount("anna-target");

        paymentController.deposit(new DepositRequest(
                source.uuid(),
                new BigDecimal("70.00"),
                "EUR"
        ));

        var transferResponse = paymentController.transfer(new TransferRequest(
                source.uuid(),
                target.uuid(),
                new BigDecimal("30.00"),
                "EUR",
                "rent"
        ));

        assertFalse(transferResponse.paymentId().isBlank());
        assertEquals("EUR", transferResponse.currency());

        var sourceBalance = accountController.getAccountBalance(source.uuid());
        var targetBalance = accountController.getAccountBalance(target.uuid());

        assertEquals(0, sourceBalance.balance().compareTo(new BigDecimal("40.00")));
        assertEquals(0, targetBalance.balance().compareTo(new BigDecimal("30.00")));
    }

    private it.bank.bankcore.account.api.response.CreateAccountResponse createAccount(String userPrefix) {
        var suffix = UUID.randomUUID().toString().substring(0, 8);

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
        ));
    }
}

