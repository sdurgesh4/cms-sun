CREATE TABLE enrollments (

                             id BIGSERIAL PRIMARY KEY,

                             student_id BIGINT NOT NULL,

                             batch_id BIGINT NOT NULL,

                             enrollment_date DATE NOT NULL,

                             agreed_fee NUMERIC(12,2) NOT NULL,

                             discount NUMERIC(12,2) NOT NULL DEFAULT 0,

                             final_fee NUMERIC(12,2) NOT NULL,

                             status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                             notes VARCHAR(1000),

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_enrollment_student
                                 FOREIGN KEY (student_id)
                                     REFERENCES students(id),

                             CONSTRAINT fk_enrollment_batch
                                 FOREIGN KEY (batch_id)
                                     REFERENCES batches(id),

                             CONSTRAINT chk_enrollment_agreed_fee
                                 CHECK (agreed_fee >= 0),

                             CONSTRAINT chk_enrollment_discount
                                 CHECK (discount >= 0),

                             CONSTRAINT chk_enrollment_final_fee
                                 CHECK (final_fee >= 0),

                             CONSTRAINT chk_enrollment_discount_limit
                                 CHECK (discount <= agreed_fee)
);

CREATE INDEX idx_enrollments_student
    ON enrollments(student_id);

CREATE INDEX idx_enrollments_batch
    ON enrollments(batch_id);

CREATE INDEX idx_enrollments_status
    ON enrollments(status);

CREATE INDEX idx_enrollments_date
    ON enrollments(enrollment_date);