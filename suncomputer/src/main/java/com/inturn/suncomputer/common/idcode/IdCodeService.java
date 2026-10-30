package com.inturn.suncomputer.common.idcode;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class IdCodeService {
    private static final Map<String, String> PREFIXES = Map.of(
            "STUDENT", "S",
            "TEACHER", "T",
            "COURSE", "C",
            "BATCH", "B",
            "ENROLLMENT", "E"
    );

    private final JdbcTemplate jdbcTemplate;

    public IdCodeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String preview(String type) {
        String normalized = normalize(type);
        String prefix = PREFIXES.get(normalized);
        int year = LocalDate.now().getYear() % 100;
        String key = prefix + String.format("%02d", year);
        Long current = jdbcTemplate.query(
                "SELECT current_value FROM id_code_sequences WHERE sequence_key = ?",
                rs -> rs.next() ? rs.getLong(1) : 0L,
                key
        );
        return format(prefix, year, (current == null ? 0L : current) + 1L);
    }

    @Transactional
    public String next(String type) {
        String normalized = normalize(type);
        String prefix = PREFIXES.get(normalized);
        int year = LocalDate.now().getYear() % 100;
        String key = prefix + String.format("%02d", year);
        Long next = jdbcTemplate.queryForObject(
                "INSERT INTO id_code_sequences(sequence_key, current_value) VALUES (?, 1) " +
                "ON CONFLICT (sequence_key) DO UPDATE SET current_value = id_code_sequences.current_value + 1 " +
                "RETURNING current_value",
                Long.class,
                key
        );
        return format(prefix, year, next == null ? 1L : next);
    }

    private String normalize(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        if (!PREFIXES.containsKey(normalized)) {
            throw new IllegalArgumentException("Unsupported ID type: " + type);
        }
        return normalized;
    }

    private String format(String prefix, int year, long sequence) {
        return prefix + String.format("%02d%03d", year, sequence);
    }
}
