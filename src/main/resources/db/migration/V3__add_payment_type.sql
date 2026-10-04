ALTER TABLE payments ADD COLUMN type VARCHAR(20);

-- Legacy deposits and withdrawals both use target_account_id.
UPDATE payments SET type = CASE
    WHEN reversed_payment_id IS NOT NULL THEN 'REVERSAL'
    WHEN source_account_id IS NOT NULL AND target_account_id IS NOT NULL THEN 'TRANSFER'
    WHEN reason = 'Withdraw' THEN 'WITHDRAW'
    ELSE 'DEPOSIT'
END;
ALTER TABLE payments ALTER COLUMN type SET NOT NULL;
ALTER TABLE payments ADD CONSTRAINT payments_type_check
    CHECK (type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER', 'REVERSAL'));
