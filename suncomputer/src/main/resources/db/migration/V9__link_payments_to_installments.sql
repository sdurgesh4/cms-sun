ALTER TABLE payments
    ADD COLUMN installment_id BIGINT;

ALTER TABLE payments
    ADD CONSTRAINT fk_payment_installment
        FOREIGN KEY (installment_id)
            REFERENCES installments(id);

CREATE INDEX idx_payments_installment
    ON payments(installment_id);