package com.inturn.suncomputer.user.controller;

import com.inturn.suncomputer.user.dto.ChangePasswordRequest;
import com.inturn.suncomputer.user.dto.ForgotPasswordRequest;
import com.inturn.suncomputer.user.dto.LoginRequest;
import com.inturn.suncomputer.user.dto.LoginResponse;
import com.inturn.suncomputer.user.dto.MeResponse;
import com.inturn.suncomputer.user.dto.MessageResponse;
import com.inturn.suncomputer.user.dto.ResetPasswordRequest;
import com.inturn.suncomputer.user.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService =
                authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            Authentication authentication
    ) {

        MeResponse response =
                authService.getCurrentUser(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        MessageResponse response =
                authService.changePassword(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        MessageResponse response =
                authService.forgotPassword(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        MessageResponse response =
                authService.resetPassword(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }
}