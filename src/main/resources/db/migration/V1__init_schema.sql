CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE,
    created_at BIGINT,
    updated_at BIGINT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    fiscal_code VARCHAR(255),
    phone_number VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    balance DECIMAL(19,2),
    currency CHAR(3),
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
    status VARCHAR(255),
    currency VARCHAR(255),
    request_code VARCHAR(255) UNIQUE,
    CONSTRAINT fk_payments_source_account FOREIGN KEY (source_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_payments_target_account FOREIGN KEY (target_account_id) REFERENCES accounts(id)
);

CREATE TABLE ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE,
    created_at BIGINT,
    updated_at BIGINT,
    account_id VARCHAR(255),
    payment_id VARCHAR(255),
    type VARCHAR(255),
    currency VARCHAR(255),
    amount DECIMAL(19,2)
);

