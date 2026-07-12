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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.show-sql=false",
        "spring.jpa.open-in-view=false"
})
class BankFlowPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bankcore_flow_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDatasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired
    private AccountController accountController;

    @Autowired
    private PaymentController paymentController;

    @Test
    void shouldCreateAccountDepositWithdrawAndReadBalanceOnPostgres() {
        var account = createAccount("postgres-flow");

        var deposit = paymentController.deposit(new DepositRequest(
                account.uuid(),
                new BigDecimal("50.00"),
                "EUR",
                requestCode()
        )).getBody();

        assertFalse(deposit.paymentId().isBlank());
        assertEquals("EUR", deposit.currency());

        var withdraw = paymentController.withdraw(new WithdrawRequest(
                account.uuid(),
                new BigDecimal("20.00"),
                "EUR",
                requestCode()
        )).getBody();

        assertFalse(withdraw.paymentId().isBlank());
        assertEquals("EUR", withdraw.currency());

        assertBalance(account.uuid(), new BigDecimal("30.00"));
    }

    @Test
    void shouldTransferBetweenTwoAccountsOnPostgres() {
        var source = createAccount("postgres-source");
        var target = createAccount("postgres-target");

        paymentController.deposit(new DepositRequest(
                source.uuid(),
                new BigDecimal("70.00"),
                "EUR",
                requestCode()
        ));

        var transfer = paymentController.transfer(new TransferRequest(
                source.uuid(),
                target.uuid(),
                new BigDecimal("35.00"),
                "EUR",
                "rent",
                requestCode()
        )).getBody();

        assertFalse(transfer.paymentId().isBlank());
        assertEquals("EUR", transfer.currency());
        assertBalance(source.uuid(), new BigDecimal("35.00"));
        assertBalance(target.uuid(), new BigDecimal("35.00"));
    }

    @Test
    void shouldBeIdempotentForRepeatedDepositRequestCodeOnPostgres() {
        var account = createAccount("postgres-idempotency");
        var code = requestCode();

        var first = paymentController.deposit(new DepositRequest(
                account.uuid(),
                new BigDecimal("25.00"),
                "EUR",
                code
        )).getBody();

        var second = paymentController.deposit(new DepositRequest(
                account.uuid(),
                new BigDecimal("25.00"),
                "EUR",
                code
        )).getBody();

        assertEquals(first.paymentId(), second.paymentId());
        assertBalance(account.uuid(), new BigDecimal("25.00"));
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
