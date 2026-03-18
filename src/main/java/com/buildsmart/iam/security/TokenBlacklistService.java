package com.buildsmart.iam.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory token blacklist implementation.
 *
 * This is suitable for development and single-node deployments.
 * For production systems, this can be replaced with a Redis-backed
 * implementation that shares the blacklist across instances.
 */
@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * Add a token to the blacklist until its expiration time.
     *
     * @param token      the JWT token value
     * @param expiresAt  the time when the token naturally expires
     */
    public void blacklistToken(String token, Date expiresAt) {
        if (token == null || token.isEmpty() || expiresAt == null) {
            return;
        }
        blacklistedTokens.put(token, expiresAt.toInstant());
    }

    /**
     * Check if a token is blacklisted and still within its blacklist window.
     * Expired entries are cleaned up lazily.
     *
     * @param token the JWT token value
     * @return true if the token is currently blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Instant now = Instant.now();
        Instant expiry = blacklistedTokens.get(token);

        if (expiry == null) {
            return false;
        }

        if (expiry.isBefore(now)) {
            // Token has naturally expired; remove from blacklist
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }
}

