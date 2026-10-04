package it.bank.bankcore.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
class FlywayPostgresMigrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bankcore_flyway_test")
            .withUsername("test")
            .withPassword("test");

    @Test
    void shouldApplyMigrationsOnCleanPostgresVolume() throws Exception {
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load();

        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration").target("2").load().migrate();
        try (var connection = java.sql.DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             var statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO accounts (id, first_name, last_name, email, fiscal_code, phone_number,
                                          city, state, country, currency)
                    VALUES (100, 'A', 'B', 'a@example.com', 'A', '+39123456', 'Rome', 'RM', 'Italy', 'EUR'),
                           (101, 'C', 'D', 'c@example.com', 'C', '+39123457', 'Rome', 'RM', 'Italy', 'EUR')
                    """);
            statement.executeUpdate("""
                    INSERT INTO payments (id, target_account_id, source_account_id, amount, reason,
                                          status, currency, request_code, reversed_payment_id)
                    VALUES (100, 100, NULL, 10, 'Deposit', 'REVERSED', 'EUR', 'legacy-deposit', NULL),
                           (101, 100, NULL, 10, 'Withdraw', 'COMPLETED', 'EUR', 'legacy-withdraw', NULL),
                           (102, 101, 100, 10, 'Withdraw', 'COMPLETED', 'EUR', 'legacy-transfer', NULL),
                           (103, NULL, 100, 10, 'correction', 'COMPLETED', 'EUR', 'legacy-reversal', 100)
                    """);
        }
        var migrateResult = flyway.migrate();
        assertTrue(migrateResult.migrationsExecuted >= 1);
        assertTrue(flyway.validateWithResult().validationSuccessful);
        try (var connection = java.sql.DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT type FROM payments ORDER BY id")) {
            for (String expected : new String[]{"DEPOSIT", "WITHDRAW", "TRANSFER", "REVERSAL"}) {
                assertTrue(rows.next());
                org.junit.jupiter.api.Assertions.assertEquals(expected, rows.getString("type"));
            }
        }
    }
}

