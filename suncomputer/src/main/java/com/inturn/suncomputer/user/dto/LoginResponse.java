package com.inturn.suncomputer.user.dto;

import java.util.List;

public record LoginResponse(

        String accessToken,

        String tokenType,

        String username,

        List<String> roles

) {
}