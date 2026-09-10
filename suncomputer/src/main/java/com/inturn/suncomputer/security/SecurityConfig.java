package com.inturn.suncomputer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/health"
                        ).permitAll()

                        // Admin only
                        .requestMatchers(
                                "/api/admin/**",
                                "/api/enquiries/**",
                                "/api/reports/**"
                        ).hasRole("ADMIN")

                        // Attendance
                        .requestMatchers(
                                "/api/attendance/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "TEACHER"
                        )

                        // Teacher APIs
                        .requestMatchers(
                                "/api/teacher/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "TEACHER"
                        )

                        // Student APIs
                        .requestMatchers(
                                "/api/student/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "STUDENT"
                        )

                        // Only admin can create notifications
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/notifications"
                        ).hasRole("ADMIN")

                        // Logged-in users can access their own notifications
                        .requestMatchers(
                                "/api/notifications/**"
                        ).authenticated()

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}