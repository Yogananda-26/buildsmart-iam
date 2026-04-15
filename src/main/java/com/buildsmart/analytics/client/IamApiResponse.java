package com.buildsmart.analytics.client;

import java.util.List;

/**
 * Wrapper DTO matching the IAM service's standard response format:
 * { "success": true, "message": "...", "data": [...] }
 */
public record IamApiResponse<T>(
        boolean success,
        String message,
        T data
) {}
