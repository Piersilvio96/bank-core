CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE,
    created_at BIGINT,
    updated_at BIGINT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    fiscal_code VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    balance DECIMAL(19,2),
    currency CHAR(3) NOT NULL,
    status VARCHAR(255)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE,
    created_at BIGINT,
    updated_at BIGINT,
    source_account_id BIGINT,
    target_account_id BIGINT,
    amount DECIMAL(19,2),
    reason TEXT,
    status VARCHAR(255) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    request_code VARCHAR(255) UNIQUE NOT NULL,
    CONSTRAINT fk_payments_source_account FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_payments_target_account FOREIGN KEY (target_account_id) REFERENCES accounts(id)
);

CREATE TABLE ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE,
    created_at BIGINT,
    updated_at BIGINT,
    account_id VARCHAR(255) NOT NULL,
    payment_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2)
);

