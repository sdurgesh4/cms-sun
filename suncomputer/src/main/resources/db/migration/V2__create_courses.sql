CREATE TABLE courses (

                         id BIGSERIAL PRIMARY KEY,

                         code VARCHAR(50) NOT NULL UNIQUE,

                         name VARCHAR(150) NOT NULL,

                         description TEXT,

                         duration INTEGER NOT NULL,

                         duration_unit VARCHAR(20) NOT NULL,

                         fee NUMERIC(12, 2) NOT NULL,

                         level VARCHAR(30) NOT NULL,

                         status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_courses_name
    ON courses(name);

CREATE INDEX idx_courses_status
    ON courses(status);

CREATE INDEX idx_courses_level
    ON courses(level);