-- The physical person (personal data) moves from accounts to a dedicated users table.
-- An account becomes just the bank account, owned by a user (1 user -> N accounts).

CREATE TABLE users (
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
    status VARCHAR(255) NOT NULL,
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_fiscal_code_key UNIQUE (fiscal_code),
    CONSTRAINT users_status_check CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

-- One user per existing account holder (accounts were unique by fiscal code / email).
INSERT INTO users (uuid, created_at, updated_at, first_name, last_name, email, fiscal_code,
                   phone_number, city, state, country, status)
SELECT DISTINCT ON (fiscal_code)
       gen_random_uuid()::text, created_at, updated_at, first_name, last_name, email, fiscal_code,
       phone_number, city, state, country, 'ACTIVE'
FROM accounts
ORDER BY fiscal_code, id;

ALTER TABLE accounts ADD COLUMN user_id BIGINT;

UPDATE accounts a
SET user_id = u.id
FROM users u
WHERE u.fiscal_code = a.fiscal_code;

ALTER TABLE accounts ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id);
CREATE INDEX idx_accounts_user_id ON accounts(user_id);

ALTER TABLE accounts
    DROP COLUMN first_name,
    DROP COLUMN last_name,
    DROP COLUMN email,
    DROP COLUMN fiscal_code,
    DROP COLUMN phone_number,
    DROP COLUMN city,
    DROP COLUMN state,
    DROP COLUMN country;
