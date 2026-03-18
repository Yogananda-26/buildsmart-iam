package com.buildsmart.iam.service;

import com.buildsmart.iam.dto.LogoutResponse;
import com.buildsmart.iam.exception.InvalidTokenException;
import com.buildsmart.iam.security.JwtService;
import com.buildsmart.iam.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
public class LogoutService {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private AuditService auditService;

    public LogoutResponse logout(String authorizationHeader, HttpServletRequest request) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        if (!jwtService.validateTokenFormat(token)) {
            throw new InvalidTokenException("Invalid JWT token");
        }

        String userId = jwtService.extractUserId(token);
        Date expiresAt = jwtService.extractExpiration(token);

        tokenBlacklistService.blacklistToken(token, expiresAt);

        auditService.logAction(userId, "LOGOUT", "AUTH", "User logged out", request);

        return new LogoutResponse(
                true,
                "Logout successful",
                LocalDateTime.now()
        );
    }
}

