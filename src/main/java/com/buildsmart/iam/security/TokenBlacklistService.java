package com.buildsmart.iam.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();


    public void blacklistToken(String token, Date expiresAt) {
        if (token == null || token.isEmpty() || expiresAt == null) {
            return;
        }
        blacklistedTokens.put(token, expiresAt.toInstant());
    }


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
            
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }
}

