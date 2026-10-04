package it.bank.bankcore.integration;

import com.jayway.jsonpath.JsonPath;
import it.bank.bankcore.payment.infrastructure.persistence.PaymentJpaRepository;
import it.bank.bankcore.ledger.infrastructure.persistence.LedgerEntryJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentIdempotencyIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private PaymentJpaRepository payments;
    @Autowired private LedgerEntryJpaRepository ledger;

    @Test
    void rejectsChangedDepositAndCrossOperationReplayWithoutMovingMoney() throws Exception {
        var account = account();
        var key = UUID.randomUUID().toString();
        String payload = deposit(account, "100", key);
        var first = mvc.perform(post("/api/v1/payments/deposit").contentType(MediaType.APPLICATION_JSON)
                .content(payload)).andExpect(status().isCreated()).andReturn();
        String id = JsonPath.read(first.getResponse().getContentAsString(), "$.paymentId");
        long paymentCount = payments.count();
        long ledgerCount = ledger.count();

        mvc.perform(post("/api/v1/payments/deposit").contentType(MediaType.APPLICATION_JSON)
                .content(deposit(account, "100.000", key)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentId").value(id));
        mvc.perform(post("/api/v1/payments/deposit").contentType(MediaType.APPLICATION_JSON)
                .content(deposit(account, "500", key)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REQUEST_CODE_CONFLICT"));
        mvc.perform(post("/api/v1/payments/withdraw").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REQUEST_CODE_CONFLICT"));
        mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                .content(reverse(id, "correction", key)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REQUEST_CODE_CONFLICT"));

        assertEquals(paymentCount, payments.count());
        assertEquals(ledgerCount, ledger.count());
        balance(account, 100);
    }

    @Test
    void reversalReplayComparesOriginalAndReasonAndPreservesBalances() throws Exception {
        var account = account();
        var deposit = mvc.perform(post("/api/v1/payments/deposit").contentType(MediaType.APPLICATION_JSON)
                .content(deposit(account, "100", UUID.randomUUID().toString())))
                .andExpect(status().isCreated()).andReturn();
        String original = JsonPath.read(deposit.getResponse().getContentAsString(), "$.paymentId");
        var key = UUID.randomUUID().toString();
        var result = mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                .content(reverse(original, " correction ", key)))
                .andExpect(status().isCreated()).andReturn();
        String reversalId = JsonPath.read(result.getResponse().getContentAsString(), "$.paymentId");
        long paymentCount = payments.count();
        long ledgerCount = ledger.count();
        mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                .content(reverse(original, "correction", key)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentId").value(reversalId));
        mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                .content(reverse(original, "different", key)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REQUEST_CODE_CONFLICT"));
        mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                .content(reverse("another-payment", "correction", key)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REQUEST_CODE_CONFLICT"));
        assertEquals(paymentCount, payments.count());
        assertEquals(ledgerCount, ledger.count());
        balance(account, 0);
    }

    @Test
    void concurrentReversalRetriesMoveMoneyOnce() throws Exception {
        concurrentReversals(true);
    }

    @Test
    void concurrentReversalsWithDifferentCodesCannotReverseTwice() throws Exception {
        concurrentReversals(false);
    }

    private void concurrentReversals(boolean sameCode) throws Exception {
        var account = account();
        var deposited = mvc.perform(post("/api/v1/payments/deposit").contentType(MediaType.APPLICATION_JSON)
                .content(deposit(account, "100", UUID.randomUUID().toString())))
                .andExpect(status().isCreated()).andReturn();
        String original = JsonPath.read(deposited.getResponse().getContentAsString(), "$.paymentId");
        var code = UUID.randomUUID().toString();
        var barrier = new java.util.concurrent.CyclicBarrier(2);
        long paymentCount = payments.count();
        long ledgerCount = ledger.count();
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<org.springframework.test.web.servlet.MvcResult> first = () -> {
                barrier.await(10, java.util.concurrent.TimeUnit.SECONDS);
                return mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                        .content(reverse(original, "correction", code))).andReturn();
            };
            java.util.concurrent.Callable<org.springframework.test.web.servlet.MvcResult> second = () -> {
                barrier.await(10, java.util.concurrent.TimeUnit.SECONDS);
                return mvc.perform(post("/api/v1/payments/reverse").contentType(MediaType.APPLICATION_JSON)
                        .content(reverse(original, "correction", sameCode ? code : UUID.randomUUID().toString())))
                        .andReturn();
            };
            var firstFuture = executor.submit(first);
            var secondFuture = executor.submit(second);
            var firstResult = firstFuture.get(20, java.util.concurrent.TimeUnit.SECONDS);
            var secondResult = secondFuture.get(20, java.util.concurrent.TimeUnit.SECONDS);
            var statuses = java.util.stream.Stream.of(firstResult, secondResult)
                    .map(result -> result.getResponse().getStatus()).sorted().toList();
            assertEquals(sameCode ? java.util.List.of(200, 201) : java.util.List.of(201, 409), statuses);
            if (sameCode) {
                assertEquals((String) JsonPath.read(firstResult.getResponse().getContentAsString(), "$.paymentId"),
                        (String) JsonPath.read(secondResult.getResponse().getContentAsString(), "$.paymentId"));
            }
        }
        assertEquals(paymentCount + 1, payments.count());
        assertEquals(ledgerCount + 1, ledger.count());
        balance(account, 0);
    }

    private String account() throws Exception {
        var unique = UUID.randomUUID().toString();
        var response = mvc.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"Test","lastName":"Customer","email":"%s@example.com",
                 "phoneNumber":"+393331234567","fiscalCode":"%s","city":"Rome",
                 "state":"RM","country":"Italy","currency":"EUR"}
                """.formatted(unique, unique))).andExpect(status().isCreated()).andReturn();
        return JsonPath.read(response.getResponse().getContentAsString(), "$.uuid");
    }
    private String deposit(String account, String amount, String code) {
        return """
                {"accountUuid":"%s","amount":%s,"currency":"EUR","requestCode":"%s"}
                """.formatted(account, amount, code);
    }
    private String reverse(String payment, String reason, String code) {
        return """
                {"paymentId":"%s","reason":"%s","requestCode":"%s"}
                """.formatted(payment, reason, code);
    }
    private void balance(String account, int expected) throws Exception {
        mvc.perform(get("/api/v1/accounts/{uuid}/balance", account)).andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(expected));
    }
}
