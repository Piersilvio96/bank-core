package it.bank.bankcore.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.show-sql=false",
        "spring.jpa.open-in-view=false"
})
class ApiMockMvcPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bankcore_test")
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
    private MockMvc mockMvc;

    @Test
    void shouldCreateAccountAndReadBalanceViaHttpApi() throws Exception {
        var uuid = createAccount("mockmvc-balance");

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void shouldCreateAccountAndReadAccountDetailsViaHttpApi() throws Exception {
        var uuid = createAccount("mockmvc-account-details");

        mockMvc.perform(get("/api/v1/accounts/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid))
                .andExpect(jsonPath("$.userUuid").isNotEmpty())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.firstName").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldCreateUserAndReadUserDetailsViaHttpApi() throws Exception {
        var userUuid = createUser("mockmvc-user-details");

        mockMvc.perform(get("/api/v1/users/{uuid}", userUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(userUuid))
                .andExpect(jsonPath("$.firstName").value("Mario"))
                .andExpect(jsonPath("$.lastName").value("Rossi"))
                .andExpect(jsonPath("$.email").value(org.hamcrest.Matchers.containsString("mockmvc-user-details")))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").isNumber());
    }

    @Test
    void shouldOpenMultipleAccountsForSameUserAndListThem() throws Exception {
        var userUuid = createUser("mockmvc-multi-account");
        var otherUserUuid = createUser("mockmvc-multi-account-other");

        var firstAccount = createAccountForUser(userUuid);
        var secondAccount = createAccountForUser(userUuid);
        createAccountForUser(otherUserUuid);

        mockMvc.perform(get("/api/v1/accounts").param("userUuid", userUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].uuid").value(firstAccount))
                .andExpect(jsonPath("$[1].uuid").value(secondAccount))
                .andExpect(jsonPath("$[0].userUuid").value(userUuid))
                .andExpect(jsonPath("$[0].currency").value("EUR"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingAccountForMissingUser() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userUuid": "%s",
                                  "currency": "EUR"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.traceId").isNotEmpty())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User not found")));
    }

    @Test
    void shouldReturnBadRequestWhenCreateUserPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Rossi",
                                  "email": "invalid-email",
                                  "phoneNumber": "123",
                                  "fiscalCode": "",
                                  "city": "Rome",
                                  "state": "RM",
                                  "country": "Italy"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldReturnInvalidRequestWhenListingAccountsWithoutUser() throws Exception {
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void shouldReturnInvalidRequestWhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/users/{uuid}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void shouldDepositAndWithdrawViaHttpApi() throws Exception {
        var uuid = createAccount("mockmvc-payment");

        mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 40.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(40.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.created").value(true));

        mockMvc.perform(post("/api/v1/payments/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 15.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(15.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.created").value(true));

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(25.00));
    }

    @Test
    void shouldReturnSamePaymentWhenDepositRequestCodeIsReused() throws Exception {
        var uuid = createAccount("mockmvc-idempotency");
        var requestCode = UUID.randomUUID().toString();

        var firstResponse = mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 25.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, requestCode)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var secondResponse = mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 25.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, requestCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String firstPaymentId = JsonPath.read(firstResponse, "$.paymentId");
        String secondPaymentId = JsonPath.read(secondResponse, "$.paymentId");
        org.junit.jupiter.api.Assertions.assertEquals(firstPaymentId, secondPaymentId);

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(25.00));
    }

    @Test
    void shouldReturnBadRequestWhenDepositPayloadIsInvalid() throws Exception {
        var uuid = createAccount("mockmvc-invalid");

        mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 0,
                                  "currency": "EU",
                                  "requestCode": null
                                }
                                """.formatted(uuid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateAccountPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userUuid": "",
                                  "currency": "EU"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldTransferViaHttpApiAndUpdateBothBalances() throws Exception {
        var sourceUuid = createAccount("mockmvc-transfer-source");
        var targetUuid = createAccount("mockmvc-transfer-target");

        mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 60.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(sourceUuid, UUID.randomUUID())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/payments/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceAccountUuid": "%s",
                                  "targetAccountUuid": "%s",
                                  "amount": 35.00,
                                  "currency": "EUR",
                                  "reason": "rent",
                                  "requestCode": "%s"
                                }
                                """.formatted(sourceUuid, targetUuid, UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(35.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.created").value(true));

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", sourceUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(25.00));

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", targetUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(35.00));
    }

    @Test
    void shouldReverseDepositViaHttpApiAndKeepIdempotency() throws Exception {
        var uuid = createAccount("mockmvc-reverse");
        var depositRequestCode = UUID.randomUUID().toString();

        var depositResponse = mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 40.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, depositRequestCode)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String depositPaymentId = JsonPath.read(depositResponse, "$.paymentId");
        var reverseRequestCode = UUID.randomUUID().toString();

        var firstReverseResponse = mockMvc.perform(post("/api/v1/payments/reverse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentId": "%s",
                                  "reason": "wrong booking",
                                  "requestCode": "%s"
                                }
                                """.formatted(depositPaymentId, reverseRequestCode)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.reversedPaymentId").value(depositPaymentId))
                .andExpect(jsonPath("$.amount").value(40.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.created").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var secondReverseResponse = mockMvc.perform(post("/api/v1/payments/reverse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentId": "%s",
                                  "reason": "wrong booking",
                                  "requestCode": "%s"
                                }
                                """.formatted(depositPaymentId, reverseRequestCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reversedPaymentId").value(depositPaymentId))
                .andExpect(jsonPath("$.created").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String firstReversePaymentId = JsonPath.read(firstReverseResponse, "$.paymentId");
        String secondReversePaymentId = JsonPath.read(secondReverseResponse, "$.paymentId");
        org.junit.jupiter.api.Assertions.assertEquals(firstReversePaymentId, secondReversePaymentId);

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0.00))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        String payload = """
                {
                  "firstName": "Mario",
                  "lastName": "Rossi",
                  "email": "duplicate.user@example.com",
                  "phoneNumber": "+393331234567",
                  "fiscalCode": "DUPLICATE123",
                  "city": "Rome",
                  "state": "RM",
                  "country": "Italy"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User already exists")));
    }

    @Test
    void shouldReturnBadRequestWhenWithdrawPayloadIsInvalid() throws Exception {
        var uuid = createAccount("mockmvc-invalid-withdraw");

        mockMvc.perform(post("/api/v1/payments/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": -10.00,
                                  "currency": "EU",
                                  "requestCode": null
                                }
                                """.formatted(uuid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldReturnBadRequestWhenTransferPayloadIsInvalid() throws Exception {
        var sourceUuid = createAccount("mockmvc-invalid-transfer-source");
        var targetUuid = createAccount("mockmvc-invalid-transfer-target");

        mockMvc.perform(post("/api/v1/payments/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceAccountUuid": "%s",
                                  "targetAccountUuid": "%s",
                                  "amount": 0,
                                  "currency": "EU",
                                  "reason": "rent",
                                  "requestCode": null
                                }
                                """.formatted(sourceUuid, targetUuid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        var missingUuid = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 15.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(missingUuid, UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Account not found")));
    }

    @Test
    void shouldReturnNotFoundWhenGetAccountUsesMissingUuid() throws Exception {
        var missingUuid = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/v1/accounts/{uuid}", missingUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Account not found")));
    }

    @Test
    void shouldReturnBadRequestWhenWithdrawAmountExceedsBalance() throws Exception {
        var uuid = createAccount("mockmvc-insufficient-funds");

        mockMvc.perform(post("/api/v1/payments/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void shouldReturnBadRequestWhenTransferUsesSameSourceAndTargetAccount() throws Exception {
        var uuid = createAccount("mockmvc-same-account-transfer");

        mockMvc.perform(post("/api/v1/payments/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceAccountUuid": "%s",
                                  "targetAccountUuid": "%s",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "reason": "same-account-check",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, uuid, UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SAME_ACCOUNT_TRANSFER"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Source and target accounts must be different")));
    }

    @Test
    void shouldReturnConflictWhenDepositCurrencyDiffersFromAccountCurrency() throws Exception {
        var uuid = createAccount("mockmvc-currency-mismatch");

        mockMvc.perform(post("/api/v1/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountUuid": "%s",
                                  "amount": 20.00,
                                  "currency": "USD",
                                  "requestCode": "%s"
                                }
                                """.formatted(uuid, UUID.randomUUID())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CURRENCY_MISMATCH"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Currency mismatch")));
    }

    @Test
    void shouldHandleConcurrentDepositRequestsWithSameRequestCode() throws Exception {
        var uuid = createAccount("mockmvc-concurrent-idempotency");
        var requestCode = UUID.randomUUID().toString();
        var payload = """
                {
                  "accountUuid": "%s",
                  "amount": 30.00,
                  "currency": "EUR",
                  "requestCode": "%s"
                }
                """.formatted(uuid, requestCode);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Callable<String> task = () -> {
                ready.countDown();
                if (!start.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Failed to synchronize concurrent deposit start");
                }
                return mockMvc.perform(post("/api/v1/payments/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
            };

            Future<String> firstFuture = executorService.submit(task);
            Future<String> secondFuture = executorService.submit(task);

            org.junit.jupiter.api.Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            var firstResponse = firstFuture.get(10, TimeUnit.SECONDS);
            var secondResponse = secondFuture.get(10, TimeUnit.SECONDS);

            String firstPaymentId = JsonPath.read(firstResponse, "$.paymentId");
            String secondPaymentId = JsonPath.read(secondResponse, "$.paymentId");
            Boolean firstCreated = JsonPath.read(firstResponse, "$.created");
            Boolean secondCreated = JsonPath.read(secondResponse, "$.created");

            org.junit.jupiter.api.Assertions.assertEquals(firstPaymentId, secondPaymentId);
            org.junit.jupiter.api.Assertions.assertTrue(Boolean.TRUE.equals(firstCreated) ^ Boolean.TRUE.equals(secondCreated));
        } finally {
            executorService.shutdownNow();
        }

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(30.00));
    }

    private String createAccount(String userPrefix) throws Exception {
        return createAccountForUser(createUser(userPrefix));
    }

    private String createUser(String userPrefix) throws Exception {
        var suffix = UUID.randomUUID().toString().substring(0, 8);

        var response = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Mario",
                                  "lastName": "Rossi",
                                  "email": "%s.%s@example.com",
                                  "phoneNumber": "+393331234567",
                                  "fiscalCode": "RSS%s",
                                  "city": "Rome",
                                  "state": "RM",
                                  "country": "Italy"
                                }
                                """.formatted(userPrefix, suffix, suffix.toUpperCase())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.uuid");
    }

    private String createAccountForUser(String userUuid) throws Exception {
        var response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userUuid": "%s",
                                  "currency": "EUR"
                                }
                                """.formatted(userUuid)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userUuid").value(userUuid))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.uuid");
    }
}
