package com.inturn.suncomputer.user.dto;

import java.util.List;

public record MeResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        boolean enabled,
        List<String> roles
) {
}