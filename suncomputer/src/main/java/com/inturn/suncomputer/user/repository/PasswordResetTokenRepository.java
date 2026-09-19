package com.inturn.suncomputer.user.repository;

import com.inturn.suncomputer.user.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(
            String tokenHash
    );

    void deleteByUserId(Long userId);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}