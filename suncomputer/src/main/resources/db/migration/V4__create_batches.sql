CREATE TABLE batches (

                         id BIGSERIAL PRIMARY KEY,

                         batch_code VARCHAR(50) NOT NULL UNIQUE,

                         course_id BIGINT NOT NULL,

                         teacher_id BIGINT NOT NULL,

                         start_date DATE NOT NULL,

                         end_date DATE,

                         start_time TIME NOT NULL,

                         end_time TIME NOT NULL,

                         room VARCHAR(100),

                         capacity INTEGER NOT NULL,

                         status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_batch_course
                             FOREIGN KEY (course_id)
                                 REFERENCES courses(id),

                         CONSTRAINT fk_batch_teacher
                             FOREIGN KEY (teacher_id)
                                 REFERENCES teachers(id),

                         CONSTRAINT chk_batch_capacity
                             CHECK (capacity > 0),

                         CONSTRAINT chk_batch_time
                             CHECK (end_time > start_time),

                         CONSTRAINT chk_batch_dates
                             CHECK (
                                 end_date IS NULL
                                     OR end_date >= start_date
                                 )
);

CREATE TABLE batch_days (

                            batch_id BIGINT NOT NULL,

                            day VARCHAR(20) NOT NULL,

                            PRIMARY KEY (batch_id, day),

                            CONSTRAINT fk_batch_days_batch
                                FOREIGN KEY (batch_id)
                                    REFERENCES batches(id)
                                    ON DELETE CASCADE
);

CREATE INDEX idx_batches_course
    ON batches(course_id);

CREATE INDEX idx_batches_teacher
    ON batches(teacher_id);

CREATE INDEX idx_batches_status
    ON batches(status);

CREATE INDEX idx_batches_start_date
    ON batches(start_date);