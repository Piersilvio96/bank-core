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
    void shouldApplyMigrationsOnCleanPostgresVolume() {
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load();

        var migrateResult = flyway.migrate();
        assertTrue(migrateResult.migrationsExecuted >= 1);
        assertTrue(flyway.validateWithResult().validationSuccessful);
    }
}

