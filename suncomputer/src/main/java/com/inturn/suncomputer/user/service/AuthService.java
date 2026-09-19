package com.inturn.suncomputer.user.service;

import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;
import com.inturn.suncomputer.security.JwtService;
import com.inturn.suncomputer.user.dto.ChangePasswordRequest;
import com.inturn.suncomputer.user.dto.ForgotPasswordRequest;
import com.inturn.suncomputer.user.dto.LoginRequest;
import com.inturn.suncomputer.user.dto.LoginResponse;
import com.inturn.suncomputer.user.dto.MeResponse;
import com.inturn.suncomputer.user.dto.MessageResponse;
import com.inturn.suncomputer.user.dto.ResetPasswordRequest;
import com.inturn.suncomputer.user.entity.PasswordResetToken;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.PasswordResetTokenRepository;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
@Transactional
public class AuthService {

    private static final int RESET_TOKEN_BYTES = 32;

    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 30;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final PasswordResetEmailService passwordResetEmailService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            PasswordResetEmailService passwordResetEmailService
    ) {
        this.authenticationManager =
                authenticationManager;

        this.userRepository =
                userRepository;

        this.passwordResetTokenRepository =
                passwordResetTokenRepository;

        this.jwtService =
                jwtService;

        this.passwordEncoder =
                passwordEncoder;

        this.passwordResetEmailService =
                passwordResetEmailService;
    }

    public LoginResponse login(
            LoginRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user =
                userRepository
                        .findByUsername(
                                request.username()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        String token =
                jwtService.generateToken(
                        user
                );

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(role ->
                                "ROLE_"
                                        + role.getName().name()
                        )
                        .toList();

        return new LoginResponse(
                token,
                "Bearer",
                user.getUsername(),
                roles
        );
    }

    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(
            String username
    ) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        return mapToMeResponse(user);
    }

    public MessageResponse changePassword(
            String username,
            ChangePasswordRequest request
    ) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        if (
                !passwordEncoder.matches(
                        request.currentPassword(),
                        user.getPassword()
                )
        ) {

            throw new BusinessRuleException(
                    "Current password is incorrect"
            );
        }

        if (
                passwordEncoder.matches(
                        request.newPassword(),
                        user.getPassword()
                )
        ) {

            throw new BusinessRuleException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        invalidateExistingTokens(user);

        return new MessageResponse(
                "Password changed successfully. Please login again."
        );
    }

    public MessageResponse forgotPassword(
            ForgotPasswordRequest request
    ) {

        String normalizedEmail =
                request.email()
                        .trim()
                        .toLowerCase();

        userRepository
                .findByEmail(normalizedEmail)
                .ifPresent(user -> {

                    if (!user.isEnabled()) {
                        return;
                    }

                    passwordResetTokenRepository
                            .deleteByUserId(
                                    user.getId()
                            );

                    String rawToken =
                            generateSecureToken();

                    PasswordResetToken resetToken =
                            new PasswordResetToken();

                    resetToken.setUser(user);

                    resetToken.setTokenHash(
                            hashToken(rawToken)
                    );

                    resetToken.setExpiresAt(
                            LocalDateTime.now()
                                    .plusMinutes(
                                            RESET_TOKEN_EXPIRATION_MINUTES
                                    )
                    );

                    resetToken.setUsed(false);

                    passwordResetTokenRepository
                            .save(resetToken);

                    passwordResetEmailService
                            .sendPasswordResetEmail(
                                    user,
                                    rawToken
                            );
                });

        /*
         * Deliberately return the same message whether
         * the email exists or not.
         *
         * This prevents account enumeration.
         */
        return new MessageResponse(
                "If an account exists for this email, a password reset link has been sent."
        );
    }

    public MessageResponse resetPassword(
            ResetPasswordRequest request
    ) {

        String tokenHash =
                hashToken(
                        request.token().trim()
                );

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHashAndUsedFalse(
                                tokenHash
                        )
                        .orElseThrow(() ->
                                new BusinessRuleException(
                                        "Invalid or expired reset token"
                                )
                        );

        if (
                resetToken.getExpiresAt()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            resetToken.setUsed(true);

            throw new BusinessRuleException(
                    "Invalid or expired reset token"
            );
        }

        User user =
                resetToken.getUser();

        if (!user.isEnabled()) {

            resetToken.setUsed(true);

            throw new BusinessRuleException(
                    "User account is disabled"
            );
        }

        if (
                passwordEncoder.matches(
                        request.newPassword(),
                        user.getPassword()
                )
        ) {

            throw new BusinessRuleException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        invalidateExistingTokens(user);

        resetToken.setUsed(true);

        return new MessageResponse(
                "Password reset successfully. Please login with your new password."
        );
    }

    private void invalidateExistingTokens(
            User user
    ) {

        long currentVersion =
                user.getTokenVersion() == null
                        ? 0L
                        : user.getTokenVersion();

        user.setTokenVersion(
                currentVersion + 1
        );
    }

    private String generateSecureToken() {

        byte[] bytes =
                new byte[RESET_TOKEN_BYTES];

        secureRandom.nextBytes(bytes);

        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }

    private MeResponse mapToMeResponse(
            User user
    ) {

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(role ->
                                role.getName().name()
                        )
                        .toList();

        return new MeResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.isEnabled(),
                roles
        );
    }
}