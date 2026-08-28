package com.inturn.suncomputer.user.service;

import com.inturn.suncomputer.security.JwtService;
import com.inturn.suncomputer.user.dto.LoginRequest;
import com.inturn.suncomputer.user.dto.LoginResponse;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService
    ) {

        this.authenticationManager =
                authenticationManager;

        this.userRepository =
                userRepository;

        this.jwtService =
                jwtService;
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

        String token =
                jwtService.generateToken(userDetails);

        User user =
                userRepository
                        .findByUsername(request.username())
                        .orElseThrow();

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(role ->
                                "ROLE_" +
                                        role.getName().name()
                        )
                        .toList();

        return new LoginResponse(
                token,
                "Bearer",
                user.getUsername(),
                roles
        );
    }
}