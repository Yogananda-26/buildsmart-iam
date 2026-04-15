package com.buildsmart.analytics.client;

/**
 * DTO representing user details fetched from the IAM module.
 * Maps to the users table in the IAM database.
 *
 * Status values: ACTIVE, INACTIVE, SUSPENDED
 * Role values: ADMIN, PROJECT_MANAGER, SITE_ENGINEER, WORKER, etc.
 */
public record UserDTO(
        String userId,
        String name,
        String email,
        String role,
        String status
) {}
