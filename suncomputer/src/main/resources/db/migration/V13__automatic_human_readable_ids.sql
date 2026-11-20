CREATE TABLE IF NOT EXISTS id_code_sequences (
    sequence_key VARCHAR(10) PRIMARY KEY,
    current_value BIGINT NOT NULL DEFAULT 0 CHECK (current_value >= 0)
);

-- Seed counters from existing codes so new codes continue after valid existing IDs.
INSERT INTO id_code_sequences(sequence_key, current_value)
SELECT 'S' || substring(student_code from 2 for 2), MAX(substring(student_code from 4)::BIGINT)
FROM students
WHERE student_code ~ '^S[0-9]{2}[0-9]+$'
GROUP BY substring(student_code from 2 for 2)
ON CONFLICT (sequence_key) DO UPDATE
SET current_value = GREATEST(id_code_sequences.current_value, EXCLUDED.current_value);

INSERT INTO id_code_sequences(sequence_key, current_value)
SELECT 'T' || substring(employee_code from 2 for 2), MAX(substring(employee_code from 4)::BIGINT)
FROM teachers
WHERE employee_code ~ '^T[0-9]{2}[0-9]+$'
GROUP BY substring(employee_code from 2 for 2)
ON CONFLICT (sequence_key) DO UPDATE
SET current_value = GREATEST(id_code_sequences.current_value, EXCLUDED.current_value);

INSERT INTO id_code_sequences(sequence_key, current_value)
SELECT 'C' || substring(code from 2 for 2), MAX(substring(code from 4)::BIGINT)
FROM courses
WHERE code ~ '^C[0-9]{2}[0-9]+$'
GROUP BY substring(code from 2 for 2)
ON CONFLICT (sequence_key) DO UPDATE
SET current_value = GREATEST(id_code_sequences.current_value, EXCLUDED.current_value);

INSERT INTO id_code_sequences(sequence_key, current_value)
SELECT 'B' || substring(batch_code from 2 for 2), MAX(substring(batch_code from 4)::BIGINT)
FROM batches
WHERE batch_code ~ '^B[0-9]{2}[0-9]+$'
GROUP BY substring(batch_code from 2 for 2)
ON CONFLICT (sequence_key) DO UPDATE
SET current_value = GREATEST(id_code_sequences.current_value, EXCLUDED.current_value);

ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS enrollment_code VARCHAR(50);

WITH ranked AS (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY EXTRACT(YEAR FROM created_at)::INTEGER ORDER BY id) AS seq,
           EXTRACT(YEAR FROM created_at)::INTEGER % 100 AS yy
    FROM enrollments
    WHERE enrollment_code IS NULL
)
UPDATE enrollments e
SET enrollment_code = 'E' || LPAD(ranked.yy::TEXT, 2, '0') || LPAD(ranked.seq::TEXT, 3, '0')
FROM ranked
WHERE e.id = ranked.id;

CREATE UNIQUE INDEX IF NOT EXISTS uk_enrollments_enrollment_code ON enrollments(enrollment_code);

INSERT INTO id_code_sequences(sequence_key, current_value)
SELECT 'E' || LPAD((EXTRACT(YEAR FROM created_at)::INTEGER % 100)::TEXT, 2, '0'),
       MAX(substring(enrollment_code from 4)::BIGINT)
FROM enrollments
WHERE enrollment_code ~ '^E[0-9]{2}[0-9]+$'
GROUP BY EXTRACT(YEAR FROM created_at)::INTEGER % 100
ON CONFLICT (sequence_key) DO UPDATE
SET current_value = GREATEST(id_code_sequences.current_value, EXCLUDED.current_value);
