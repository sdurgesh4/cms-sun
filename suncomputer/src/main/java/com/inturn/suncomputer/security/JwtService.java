package com.inturn.suncomputer.security;

import com.inturn.suncomputer.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long jwtExpiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long jwtExpiration
    ) {

        this.secretKey =
                Keys.hmacShaKeyFor(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        this.jwtExpiration =
                jwtExpiration;
    }

    public String generateToken(
            User user
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        claims.put(
                "roles",
                user.getRoles()
                        .stream()
                        .map(role ->
                                "ROLE_"
                                        + role.getName().name()
                        )
                        .toList()
        );

        claims.put(
                "tokenVersion",
                user.getTokenVersion()
        );

        Date now =
                new Date();

        Date expirationDate =
                new Date(
                        now.getTime()
                                + jwtExpiration
                );

        return Jwts.builder()
                .claims(claims)
                .subject(
                        user.getUsername()
                )
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public Date extractExpiration(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    public Long extractTokenVersion(
            String token
    ) {

        return extractClaim(
                token,
                claims -> {

                    Object value =
                            claims.get(
                                    "tokenVersion"
                            );

                    if (value instanceof Number number) {
                        return number.longValue();
                    }

                    return null;
                }
        );
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername()
        )
                && !isTokenExpired(token);
    }

    public boolean isTokenVersionValid(
            String token,
            User user
    ) {

        Long tokenVersion =
                extractTokenVersion(token);

        return tokenVersion != null
                && tokenVersion.equals(
                user.getTokenVersion()
        );
    }

    private boolean isTokenExpired(
            String token
    ) {

        return extractExpiration(token)
                .before(
                        new Date()
                );
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(
                claims
        );
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}