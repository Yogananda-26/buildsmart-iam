package com.buildsmart.analytics.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Utility class to parse and validate JWT tokens issued by the IAM module.
 * This service does NOT create tokens — only the IAM module does that.
 * It validates the token signature and extracts claims (username, roles).
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // Use raw UTF-8 bytes — must match how the IAM module signs tokens
        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Parse all claims from the token. Throws JwtException if invalid/expired.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract the username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract roles from the token.
     * Handles multiple claim names: "roles", "role", "authorities", "scope"
     * Handles multiple formats: List, single String, comma-separated String
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);

        // Try different claim names that IAM modules commonly use
        Object roles = claims.get("roles");
        if (roles == null) roles = claims.get("role");
        if (roles == null) roles = claims.get("authorities");
        if (roles == null) roles = claims.get("scope");

        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream().map(Object::toString).toList();
        }
        if (roles instanceof String str) {
            // Handle comma-separated: "ADMIN,PROJECT_MANAGER" or single: "ADMIN"
            return List.of(str.split(","));
        }
        return List.of();
    }

    /**
     * Extract userId from the token (e.g., "BSAD001").
     * The IAM module stores this in the "userId" claim.
     */
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userId = claims.get("userId");
        return userId != null ? userId.toString() : null;
    }

    /**
     * Extract user's display name from the token.
     */
    public String extractName(String token) {
        Claims claims = extractAllClaims(token);
        Object name = claims.get("name");
        return name != null ? name.toString() : null;
    }

    /**
     * Check if the token is expired.
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validate the token: checks signature, expiry, and that a subject exists.
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject() != null && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
