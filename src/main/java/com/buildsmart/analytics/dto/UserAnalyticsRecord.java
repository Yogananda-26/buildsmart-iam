package com.buildsmart.analytics.dto;

import com.buildsmart.analytics.client.UserDTO;
import java.util.List;
import java.util.Map;

/**
 * DTO for user analytics data — summarizes Active, Inactive, and Suspended users
 * from the IAM module's user table.
 */
public record UserAnalyticsRecord(
        int totalUsers,
        int activeUsers,
        int inactiveUsers,
        int suspendedUsers,
        Map<String, Integer> usersByRole,
        Map<String, Map<String, Integer>> statusByRole,
        List<UserDTO> users
) {}
