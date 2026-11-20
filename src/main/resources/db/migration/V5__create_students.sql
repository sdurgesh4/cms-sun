CREATE TABLE students (

                          id BIGSERIAL PRIMARY KEY,

                          user_id BIGINT NOT NULL UNIQUE,

                          student_code VARCHAR(50) NOT NULL UNIQUE,

                          date_of_birth DATE,

                          gender VARCHAR(20),

                          address VARCHAR(500),

                          city VARCHAR(100),

                          state VARCHAR(100),

                          pincode VARCHAR(10),

                          parent_name VARCHAR(200),

                          parent_phone VARCHAR(20),

                          admission_date DATE NOT NULL,

                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_student_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
);

CREATE INDEX idx_students_status
    ON students(status);

CREATE INDEX idx_students_student_code
    ON students(student_code);

CREATE INDEX idx_students_parent_phone
    ON students(parent_phone);