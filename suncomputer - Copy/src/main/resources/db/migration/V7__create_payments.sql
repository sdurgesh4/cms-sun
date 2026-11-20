CREATE TABLE payments (

                          id BIGSERIAL PRIMARY KEY,

                          enrollment_id BIGINT NOT NULL,

                          receipt_number VARCHAR(50) NOT NULL UNIQUE,

                          payment_date DATE NOT NULL,

                          amount NUMERIC(12,2) NOT NULL,

                          payment_method VARCHAR(30) NOT NULL,

                          transaction_reference VARCHAR(150),

                          status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',

                          notes VARCHAR(1000),

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_payment_enrollment
                              FOREIGN KEY (enrollment_id)
                                  REFERENCES enrollments(id),

                          CONSTRAINT chk_payment_amount
                              CHECK (amount > 0)
);

CREATE INDEX idx_payments_enrollment
    ON payments(enrollment_id);

CREATE INDEX idx_payments_payment_date
    ON payments(payment_date);

CREATE INDEX idx_payments_status
    ON payments(status);

CREATE INDEX idx_payments_transaction_reference
    ON payments(transaction_reference);