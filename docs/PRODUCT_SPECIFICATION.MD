 # BankCore Product Specification

Status: Initial specification for the demonstration product.

This document defines the intended behavior of BankCore. It is a target specification, not a statement that every feature is already implemented. Implementation work is organized in `../ROADMAP_BANKCORE.md`.

## 1. Product Purpose

BankCore is a demonstration banking backend that manages customers, accounts, and simulated money movements. It provides APIs for a customer portal and an operator portal.

The initial product uses a modular monolith: customer, account, payment, ledger, and audit responsibilities have clear boundaries within one backend. Financial updates use a shared transactional persistence boundary.

The product must make financial operations explainable, authorized, repeatable without duplication, and recoverable after failures.

## 2. Initial Scope

The initial release supports:

- Individual customers.
- Multiple accounts per customer, with one owner per account.
- EUR accounts and payments.
- Account creation, account details, balances, and transaction history.
- Transfers between accounts managed by BankCore.
- Operator-controlled simulated deposits and withdrawals.
- Full payment reversals.
- Account blocking, unblocking, and closure.
- Customer authentication, operator authentication, and resource-level authorization.
- Financial ledger recording and operational audit trails.

The following are outside the initial scope:

- Real money, external bank transfers, and operational IBAN issuance.
- Cards, direct debits, loans, and overdrafts.
- Joint accounts and business customers.
- Foreign exchange and additional currencies.
- Fees, interest, and partial reversals.

Real-money services require a separate specification covering partners, regulatory responsibilities, settlement, and reconciliation.

## 3. Customers and Account Ownership

A customer represents an individual person. An authentication identity represents access to the system. An account represents a financial balance. These concepts must remain separate.

Customer data includes name, fiscal code, contact details, and address information. Account data includes its identifier, owner, currency, balance, and status.

Rules:

- A customer may own multiple accounts.
- Each account has exactly one owner in the initial release.
- Fiscal code uniquely identifies a customer within the supported demonstration dataset. Its normalization must be defined before implementation.
- Customer email is unique after trimming whitespace and applying the product's case-insensitive comparison policy.
- Opening another account for an existing customer must not create another customer record or fail because the customer's email or fiscal code already exists.
- An account owner cannot be changed through a general account update endpoint.
- Customer profile updates cannot modify account balances, currencies, or financial history.
- Existing account-embedded personal data must be migrated when the customer model is introduced.

## 4. Monetary Rules

| Property | Initial rule |
|---|---|
| Supported currency | EUR |
| Initial balance | EUR 0.00 |
| Negative balances | Forbidden |
| Payment amount | Strictly positive |
| Precision | Exact multiples of EUR 0.01 |
| Excess precision | Rejected; no implicit rounding |
| Fees and interest | None |
| Currency changes | Not allowed after account creation |

Monetary calculations must use exact decimal arithmetic. Values such as `10.001` are invalid, while `10`, `10.00`, and `10.000` represent valid cent-exact amounts.

The initial demonstration limit is EUR 10,000.00 per deposit, withdrawal, or transfer. The maximum account balance is EUR 1,000,000.00. Both limits must be configurable and validated against database capacity. These are demonstration defaults, not regulatory limits.

There are no daily or monthly aggregate limits in the initial release. Adding them requires a separate policy and concurrency-safe enforcement.

A full reversal uses the original payment amount rather than the current per-payment limit. It must still respect non-negative balances and the maximum account balance.

In the initial release, payments complete synchronously and there are no reserved funds. If pending payments are introduced, ledger balance, reserved funds, and available balance must be specified separately.

## 5. Roles and Permissions

| Operation | Customer | Operator |
|---|---|---|
| View account details, balance, and history | Owned accounts only | Requires an explicit account-read permission |
| Open an account | For their own customer profile | Requires an explicit account-open permission |
| Update customer contact details | Own profile | Requires an explicit customer-update permission |
| Transfer funds | From an owned account | Requires an explicit payment-initiation permission |
| Create simulated deposits or withdrawals | Not allowed | Requires an explicit simulation-payment permission |
| Reverse a payment | Not allowed | Requires an explicit reversal permission and a reason |
| Block or unblock an account | Not allowed | Requires an explicit account-management permission and a reason |
| Close an account | Owned accounts, subject to closure rules | Requires an explicit account-management permission |

The recipient of a transfer does not need to belong to the sender. Knowing an account identifier does not grant access to its balance, profile, or history.

All permissions must be enforced by the backend. Portal visibility is not an authorization mechanism. Operator or administrator labels must not automatically grant unrestricted financial access.

Unauthenticated requests return `401`. Unauthorized access returns `403`, or `404` where hiding another customer's resource is the chosen disclosure policy.

## 6. Account Lifecycle

| Status | Ordinary credits | Ordinary debits | Read access |
|---|---|---|---|
| ACTIVE | Allowed | Allowed | Subject to authorization |
| BLOCKED | Allowed | Forbidden | Subject to authorization |
| CLOSED | Forbidden | Forbidden | Subject to authorization |

Accounts are created as ACTIVE with a zero balance.

Allowed transitions:

- ACTIVE to BLOCKED, with an authorized operator and a reason.
- BLOCKED to ACTIVE, with an authorized operator and a reason.
- ACTIVE to CLOSED, subject to closure rules.

Closure requires a balance of exactly zero and no pending operations or reserved funds. A blocked account must be reviewed and unblocked before it can be closed. Closed accounts cannot be reopened in the initial release.

Closed accounts and their financial history must be retained. Account closure must not delete financial records.

## 7. Payment Behavior

### Deposit

A deposit is an operator-controlled simulation of incoming funds. It credits an ACTIVE or BLOCKED account and records the payment and its ledger entries atomically.

Customers cannot credit their accounts by calling a deposit endpoint. Real incoming funds are outside this release.

### Withdrawal

A withdrawal is an operator-controlled simulation of outgoing funds. It requires an ACTIVE account and sufficient funds.

### Transfer

A transfer moves funds between two different BankCore accounts. The sender must be authorized to debit the source account. The source must be ACTIVE; the recipient may be ACTIVE or BLOCKED.

The payment currency must match both account currencies. A transfer without fees reduces the source balance and increases the recipient balance by exactly the same amount.

### Completion and Atomicity

New successful payments are COMPLETED. Payment records, account balances, and ledger entries must be committed together. Any failure must roll back all financial changes.

A validation failure must not create a completed payment or a partial ledger record. Failed attempts may be recorded separately in the operational audit trail.

## 8. Idempotency

Every financial mutation requires a non-blank `requestCode` matching `^[A-Za-z0-9._:-]{8,64}$`.

For the initial release, request codes are globally unique across payment types. They are identifiers, not credentials, and must be checked only in an authorized context.

- A repeated authorized request with the same code and equivalent operation data returns the original payment without applying another movement.
- Reusing a code with different operation data returns `409 REQUEST_CODE_CONFLICT`.
- Significant operation data includes operation type, involved accounts, amount, currency, original payment identifier for reversals, and reason where applicable.
- Decimal representations of the same amount are equivalent. Optional empty reasons are normalized to an absent reason; non-empty reasons are trimmed before comparison.
- Unauthorized callers cannot retrieve another caller's payment through a request code.
- Concurrent requests must produce at most one financial effect.
- A new payment returns `201`; an accepted replay returns `200`.

Clients must reuse the original code after an uncertain response. They must not generate a new code merely because a timeout occurred.

## 9. Reversals

A reversal creates a new compensating payment. It preserves the original payment and its ledger entries.

Rules:

- Only an authorized operator may initiate a reversal, with a non-blank reason.
- Only a COMPLETED original payment may be reversed.
- Only full reversals are supported.
- An original payment may have at most one full reversal, including under concurrent requests with different request codes.
- A reversal payment cannot itself be reversed in the initial release.
- Every account involved in a reversal must be ACTIVE. Reversals involving BLOCKED or CLOSED accounts are rejected and require operational review.
- Any debit required by the reversal must have sufficient funds.
- The original payment becomes REVERSED only when the compensating payment, balance updates, links, and ledger entries commit successfully.
- Retrying the same authorized reversal request returns the existing reversal.

Example: a deposit of EUR 100 followed by a withdrawal of EUR 80 leaves EUR 20. Reversing the deposit is rejected because it requires a debit of EUR 100. No balance, payment status, or ledger entry may change.

## 10. Financial Invariants

The following conditions must always hold:

1. Account balances are never negative.
2. Recorded amounts use the supported currency and monetary precision.
3. A fee-free transfer conserves the combined balance of the involved customer accounts.
4. A financial operation commits all required records or none.
5. An idempotent replay never produces another financial movement.
6. An original payment has at most one full reversal.
7. Customers cannot read or debit accounts they do not own.
8. Corrections preserve the original financial history.
9. Every completed financial operation has the required ledger entries.
10. Complete accounting journal entries balance debits and credits per currency, including the appropriate counterpart accounts for deposits and withdrawals.
11. Account balances can be reconciled with the authoritative accounting records.

The current customer-movement ledger must evolve to support the accounting counterparties required by invariants 10 and 11.

## 11. History, Audit, and API Errors

Customers can view authorized account history with pagination, a stable ordering, and filters for time period and operation type. Entries expose amount, currency, timestamp, reason, status, and reversal relationships where applicable.

Timestamps represent UTC instants. Presentation layers may convert them to the user's time zone.

Sensitive operations must produce audit records containing actor identity, action, resource, timestamp, outcome, reason where required, and correlation identifier. Audit records must not contain passwords, access tokens, or unnecessary personal data.

API responses use stable error codes:

| HTTP status | Meaning | Example code |
|---|---|---|
| 400 | Invalid input | INVALID_AMOUNT |
| 401 | Authentication required or invalid | AUTHENTICATION_REQUIRED |
| 403 | Permission denied | ACCESS_DENIED |
| 404 | Resource not found or deliberately concealed | ACCOUNT_NOT_FOUND |
| 409 | Conflict with current state or request identity | REQUEST_CODE_CONFLICT |
| 500 | Unexpected internal failure | INTERNAL_ERROR |

Insufficient funds, invalid account state, and already-reversed payments are state conflicts and return `409`. Internal exception messages and database details must not be exposed to clients.

## 12. Acceptance Scenarios

| Scenario | Expected outcome |
|---|---|
| A customer opens their first account | ACTIVE account with EUR 0.00 balance |
| The same customer opens a second account | Allowed without creating a duplicate customer |
| An authorized operator deposits EUR 100 | Balance increases by EUR 100; payment and ledger commit together |
| A customer attempts a simulated deposit | Access denied; no financial changes |
| A customer transfers EUR 30 from a EUR 100 account | Source becomes EUR 70; recipient gains EUR 30 |
| A transfer uses the same source and recipient | Rejected; no financial changes |
| A withdrawal of EUR 120 targets a EUR 100 account | Insufficient-funds conflict; no financial changes |
| A payment amount is EUR 10.001 | Invalid-input response; no implicit rounding |
| An identical request is repeated with the same code | Original payment returned; no duplicate movement |
| The same code is reused for another amount or operation type | Request-code conflict |
| Two identical requests arrive concurrently | At most one financial movement |
| Two full reversals of one payment arrive with different codes | At most one succeeds; the other returns a conflict |
| A transfer recipient is BLOCKED | Credit allowed if all other rules pass |
| A transfer source is BLOCKED | Rejected; no financial changes |
| A reversal involves a BLOCKED account | Rejected; operational review required |
| Closure is requested with a positive balance | Rejected |
| Closure is requested for an ACTIVE zero-balance account with no pending operations | Account becomes CLOSED; history remains available |
| A customer requests another customer's balance | Access denied or resource concealed; no data disclosure |
| Ledger recording fails during a payment | Payment and all balance updates roll back |

## 13. Definition of Completion

The demonstration product is complete when the in-scope behaviors are implemented, documented, and verified by meaningful tests, including PostgreSQL integration tests for transactions, constraints, and concurrency.

Completion also requires reproducible startup, authenticated APIs, operational monitoring, and a tested backup and restore procedure appropriate to the demonstration environment.

Changes to these rules must update this specification, affected tests, and API documentation. Support for real money requires a separate readiness assessment and cannot be inferred from completion of this demonstration specification.
