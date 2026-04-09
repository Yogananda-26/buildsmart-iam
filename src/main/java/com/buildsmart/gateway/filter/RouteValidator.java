package com.buildsmart.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteValidator {

    // Endpoints that do NOT require JWT authentication
    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/validate-reset-token",
            "/actuator/health",
            "/swagger-ui",
            "/v3/api-docs",
            "/api-docs"
    );

    public boolean isOpenEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return OPEN_ENDPOINTS.stream()
                .anyMatch(path::startsWith);
    }
}
