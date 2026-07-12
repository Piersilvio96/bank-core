ALTER TABLE payments
ADD COLUMN reversal_payment_id BIGINT,
ADD CONSTRAINT fk_payments_reversal_payment FOREIGN KEY (reversal_payment_id) REFERENCES payments(id);

ALTER TABLE payments
ADD COLUMN reversed_payment_id BIGINT,
ADD CONSTRAINT fk_payments_reversed_payment FOREIGN KEY (reversed_payment_id) REFERENCES payments(id);

ALTER TABLE payments
    DROP CONSTRAINT IF EXISTS payments_status_check;

ALTER TABLE payments
    ADD CONSTRAINT payments_status_check
        CHECK ((status)::text = ANY
               ((ARRAY ['PENDING'::character varying, 'COMPLETED'::character varying, 'REVERSED'::character varying, 'FAILED'::character varying])::text[]));