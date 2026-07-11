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
                .andExpect(jsonPath("$.firstName").value("Mario"))
                .andExpect(jsonPath("$.lastName").value("Rossi"))
                .andExpect(jsonPath("$.email").value(org.hamcrest.Matchers.containsString("mockmvc-account-details")))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(40.00))
                .andExpect(jsonPath("$.currency").value("EUR"));

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(15.00))
                .andExpect(jsonPath("$.currency").value("EUR"));

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
                .andExpect(status().isOk())
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
                                  "firstName": "",
                                  "lastName": "Rossi",
                                  "email": "invalid-email",
                                  "phoneNumber": "123",
                                  "fiscalCode": "",
                                  "city": "Rome",
                                  "state": "RM",
                                  "country": "Italy",
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
                .andExpect(status().isOk());

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(35.00))
                .andExpect(jsonPath("$.currency").value("EUR"));

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", sourceUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(25.00));

        mockMvc.perform(get("/api/v1/accounts/{uuid}/balance", targetUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(35.00));
    }

    @Test
    void shouldReturnConflictWhenAccountAlreadyExists() throws Exception {
        String payload = """
                {
                  "firstName": "Mario",
                  "lastName": "Rossi",
                  "email": "duplicate.account@example.com",
                  "phoneNumber": "+393331234567",
                  "fiscalCode": "DUPLICATE123",
                  "city": "Rome",
                  "state": "RM",
                  "country": "Italy",
                  "currency": "EUR"
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Account already exists")));
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
    void shouldReturnInternalServerErrorWhenAccountDoesNotExist() throws Exception {
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
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Account not found")));
    }

    private String createAccount(String userPrefix) throws Exception {
        var suffix = UUID.randomUUID().toString().substring(0, 8);

        var response = mockMvc.perform(post("/api/v1/accounts")
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
                                  "country": "Italy",
                                  "currency": "EUR"
                                }
                                """.formatted(userPrefix, suffix, suffix.toUpperCase())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.uuid");
    }
}

