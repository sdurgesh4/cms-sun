CREATE TABLE teachers (

                          id BIGSERIAL PRIMARY KEY,

                          user_id BIGINT NOT NULL UNIQUE,

                          employee_code VARCHAR(50) NOT NULL UNIQUE,

                          specialization VARCHAR(200),

                          qualification VARCHAR(200),

                          experience_years INTEGER,

                          joining_date DATE,

                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_teacher_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
);

CREATE INDEX idx_teachers_status
    ON teachers(status);

CREATE INDEX idx_teachers_specialization
    ON teachers(specialization);
