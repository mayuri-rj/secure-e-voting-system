package com.evoting.securevoting.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Secret key (must be at least 32 characters for HS256)
    private final String SECRET = "SecureVotingSuperSecretKeyForJwt1234567890";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token valid for 1 hour
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 🔹 Generate Token
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Extract Email
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 🔹 Extract Role
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 🔹 Validate Token
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 🔹 Get Claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}