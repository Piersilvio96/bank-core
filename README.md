# BankCore

[![Test on main](https://github.com/Piersilvio96/bank-core/actions/workflows/test-on-main.yml/badge.svg)](https://github.com/Piersilvio96/bank-core/actions/workflows/test-on-main.yml)

BankCore is the backend core of a modular banking platform built with a clean architecture style. It currently includes account management, payments, and ledger recording, with PostgreSQL persistence and versioned schema migrations.

## What Is Implemented

- Users (the physical person: name, email, fiscal code, address) with duplicate prevention (`fiscalCode` or `email`).
- Accounts owned by a user (one user, many accounts); an account only holds balance, currency and status.
- Account read APIs (`GET account`, `GET balance`, list accounts of a user).
- Payment flows:
  - deposit,
  - withdraw,
  - transfer.
- Ledger entries for each payment operation.
- Full payment reversals with original-payment locking.
- Idempotency by request code, with conflicts for different payment data.
- Global API error handling (`400`, `404`, `409`, `500`) with a stable machine-readable `code` and a `traceId` in every error response.

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Flyway
- Gradle
- Lombok
- Springdoc OpenAPI
- Docker Compose
- Testcontainers (PostgreSQL)
- ArchUnit

## Architecture

```text
src/main/java/it/bank/bankcore
|-- BankCoreApplication.java
|-- user|account|payment|ledger
|   |-- api
|   |   |-- controller
|   |   |-- request
|   |   |-- response
|   |   `-- mapper
|   |-- application
|   |   |-- usecase
|   |   |-- command
|   |   |-- query
|   |   `-- result
|   |-- domain
|   |   |-- model
|   |   |-- repository
|   |   |-- exception
|   |   `-- mapper
|   `-- infrastructure
|       `-- persistence
`-- shared
    |-- api
    |-- application
    |-- domain
    |-- exception
    `-- infrastructure
```

### Layer Conventions

- `api`: controllers + request/response DTO + API mappers.
- `application`: use cases, commands/queries/results, orchestration.
- `domain`: business models, rules, domain exceptions, repository contracts.
- `infrastructure`: JPA entities/repositories/mappers and DB adapters.
- `shared`: common abstractions and exception handling.

## ArchUnit Rules

The test suite includes architecture checks in `src/test/java/it/bank/bankcore/architecture/ArchitectureTest.java`:

- application layer must not depend on api layer,
- api layer must not depend on infrastructure layer,
- use cases must implement the shared `UseCase` contract,
- package slices must be free of cycles.

## Database and Migrations

### Runtime configuration

- PostgreSQL datasource is defined in `src/main/resources/application.yaml`.
- JPA schema mode is:

```yaml
spring.jpa.hibernate.ddl-auto: validate
```

- Flyway is enabled and scans:

```yaml
spring.flyway.locations: classpath:db/migration
```

### Migration files

- `src/main/resources/db/migration/V1__init_schema.sql` creates:
  - `accounts`,
  - `payments`,
  - `ledger_entries`.
- `V2__add_reversal_columns.sql` links original and reversal payments.
- `V3__add_payment_type.sql` stores payment types and backfills existing payments.
- `V4__split_users_from_accounts.sql` creates `users`, moves the personal data out of `accounts`
  (one user per existing account holder) and links each account to its owner with `accounts.user_id`.

## Requirements

- JDK 21
- Docker + Docker Compose
- Gradle Wrapper (already in repo)

## Local Setup

Start PostgreSQL:

```powershell
docker compose up -d
```

Run application (Windows PowerShell):

```powershell
.\gradlew.bat bootRun
```

Application base URL:

```text
http://localhost:8080
```

## API Overview

### User APIs

A user is the physical person. Create the user first, then open accounts for it.

- `POST /api/v1/users`
- `GET /api/v1/users/{uuid}`

Create user payload example:

```json
{
  "firstName": "Mario",
  "lastName": "Rossi",
  "email": "mario.rossi@example.com",
  "phoneNumber": "+393331234567",
  "fiscalCode": "RSSMRA80A01H501U",
  "city": "Rome",
  "state": "RM",
  "country": "Italy"
}
```

### Account APIs

- `POST /api/v1/accounts`
- `GET /api/v1/accounts?userUuid={userUuid}` lists the accounts of a user
- `GET /api/v1/accounts/{uuid}`
- `GET /api/v1/accounts/{uuid}/balance`

Create account payload example (`currency` is optional and defaults to `EUR`):

```json
{
  "userUuid": "0b6f3a52-7a40-4d5b-8c12-b04e9a1c2d3e",
  "currency": "EUR"
}
```

### Payment APIs

- `POST /api/v1/payments/deposit`
- `POST /api/v1/payments/withdraw`
- `POST /api/v1/payments/transfer`
- `POST /api/v1/payments/reverse`

New payments return `201`; identical retries return `200`. Reusing a request code
with different operation data returns `409` with code `REQUEST_CODE_CONFLICT`.
Payment type, accounts, amount, currency, and applicable reason are compared;
reversals also compare the original payment ID. Duplicate insert recovery rolls
back the failed transaction before reading the existing payment in a new one.
Each payment use case owns its transaction, independently of any caller transaction.

Deposit payload example:

```json
{
  "accountUuid": "a1b2c3d4-...",
  "amount": 25.00,
  "currency": "EUR",
  "requestCode": "REQ-123"
}
```

Transfer payload example:

```json
{
  "sourceAccountUuid": "source-uuid",
  "targetAccountUuid": "target-uuid",
  "amount": 30.00,
  "currency": "EUR",
  "reason": "rent",
  "requestCode": "REQ-TRANSFER-1"
}
```

## Validation and Error Mapping

- API DTO validation errors return `400`.
- Business rule constraint violations (e.g. duplicate account) return `409`.
- Unhandled infrastructure/runtime errors return `500`.

Handled by `shared.exception.SharedExceptionHandler`.

## Testing

### Main test types in project

- Unit tests for domain/use case/mapper components.
- Integration flow tests (`BankFlowIntegrationTest`).
- API tests with `MockMvc` + `Testcontainers` PostgreSQL (`ApiMockMvcPostgresIntegrationTest`).
- Architecture tests with ArchUnit (`ArchitectureTest`).

### Run all tests (Windows PowerShell)

```powershell
.\gradlew.bat test --no-daemon
```

### Run only MockMvc + PostgreSQL API tests

```powershell
.\gradlew.bat test --tests "it.bank.bankcore.integration.ApiMockMvcPostgresIntegrationTest" --no-daemon
```

## OpenAPI

When the app is running:

```text
http://localhost:8080/swagger-ui/index.html
```

## Current Status

The project currently has account, payment, and ledger flows running with:

- PostgreSQL persistence,
- Flyway migrations,
- architecture guardrails (ArchUnit),
- API integration tests with MockMvc + Testcontainers.

Next improvements can focus on security/auth, monetary limits, and richer operational observability.
