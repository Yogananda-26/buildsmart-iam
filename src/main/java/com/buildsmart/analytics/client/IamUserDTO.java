
package com.buildsmart.analytics.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Full DTO matching the IAM service's actual user entity response.
 * Includes all fields returned by /admin/users.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IamUserDTO(
        String userId,
        String name,
        String email,
        String phone,
        String role,
        String status,
        String createdAt,
        String updatedAt
) {}
