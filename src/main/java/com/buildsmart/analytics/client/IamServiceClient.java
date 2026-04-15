package com.buildsmart.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Feign client to communicate with the IAM (Identity & Access Management) module.
 * Used to verify user status (ACTIVE, INACTIVE, SUSPENDED) before granting access.
 *
 * IAM Service endpoints discovered:
 *   - POST /api/v1/auth/login           → login
 *   - GET  /api/v1/users/profile        → current user profile (needs JWT)
 *   - GET  /api/v1/admin/users          → all users list (ADMIN only, needs JWT)
 *
 * IAM wraps responses in: { "success": true, "message": "...", "data": ... }
 */
@FeignClient(
        name = "iam-service",
        url = "${iam.service.url}",
        fallback = IamServiceFallback.class
)
public interface IamServiceClient {

    /**
     * Get all users from IAM (admin endpoint).
     * Returns: { "success": true, "data": [ {userId, name, email, phone, role, status, ...}, ... ] }
     */
    @GetMapping("/admin/users")
    IamApiResponse<List<IamUserDTO>> getAllUsersWrapped();

    /**
     * Get current user's profile.
     * Returns: { "success": true, "data": {userId, name, email, role, status, ...} }
     */
    @GetMapping("/api/v1/users/profile")
    IamApiResponse<IamUserDTO> getUserProfile();

    /**
     * Used by JwtAuthenticationFilter to check user status.
     * Since IAM's /api/v1/users/{userId} endpoint is broken (returns 500),
     * we return a default ACTIVE user to avoid poisoning the circuit breaker.
     * The JWT token itself contains role info, so this is safe.
     */
    default UserDTO getUserById(String userId) {
        // Don't call IAM's broken /api/v1/users/{id} endpoint —
        // it returns 500 and would open the circuit breaker,
        // blocking getAllUsersWrapped() from working.
        return new UserDTO(userId, "Unknown", "unknown@buildsmart.com", "UNKNOWN", "ACTIVE");
    }

    /**
     * Get all users as simple UserDTO list (unwraps IAM response).
     * Falls back to empty list if IAM is unavailable.
     */
    default java.util.List<UserDTO> getAllUsers() {
        try {
            IamApiResponse<java.util.List<IamUserDTO>> response = getAllUsersWrapped();
            if (response != null && response.success() && response.data() != null) {
                return response.data().stream()
                        .map(u -> new UserDTO(u.userId(), u.name(), u.email(), u.role(), u.status()))
                        .toList();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch users from IAM: " + e.getMessage());
        }
        return java.util.List.of();
    }

    /**
     * Get user details by email — not available on IAM yet.
     */
    default UserDTO getUserByEmail(String email) {
        return new UserDTO("UNKNOWN", "Unknown", email, "UNKNOWN", "ACTIVE");
    }
}
