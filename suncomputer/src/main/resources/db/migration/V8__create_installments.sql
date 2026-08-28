CREATE TABLE installments (

                              id BIGSERIAL PRIMARY KEY,

                              enrollment_id BIGINT NOT NULL,

                              installment_number INTEGER NOT NULL,

                              due_date DATE NOT NULL,

                              amount NUMERIC(12,2) NOT NULL,

                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

                              notes VARCHAR(1000),

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_installment_enrollment
                                  FOREIGN KEY (enrollment_id)
                                      REFERENCES enrollments(id),

                              CONSTRAINT chk_installment_number
                                  CHECK (installment_number > 0),

                              CONSTRAINT chk_installment_amount
                                  CHECK (amount > 0),

                              CONSTRAINT uq_enrollment_installment_number
                                  UNIQUE (
                                          enrollment_id,
                                          installment_number
                                      )
);

CREATE INDEX idx_installments_enrollment
    ON installments(enrollment_id);

CREATE INDEX idx_installments_due_date
    ON installments(due_date);

CREATE INDEX idx_installments_status
    ON installments(status);