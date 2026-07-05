package it.bank.bankcore.integration;

import it.bank.bankcore.account.api.controller.AccountController;
import it.bank.bankcore.account.api.request.CreateAccountRequest;
import it.bank.bankcore.payment.api.controller.PaymentController;
import it.bank.bankcore.payment.api.request.DepositRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

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
        var createResponse = accountController.createAccount(new CreateAccountRequest(
                "Mario",
                "Rossi",
                "mario.rossi@example.com",
                "+393331234567",
                "RSSMRA80A01H501U",
                "Rome",
                "RM",
                "Italy",
                "EUR"
        ));

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
}

