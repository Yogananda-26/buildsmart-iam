package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.client.IamRestClient;
import com.buildsmart.analytics.client.UserDTO;
import com.buildsmart.analytics.dto.UserAnalyticsRecord;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exposes IAM user analytics — Active, Inactive, Suspended users
 * with breakdowns by role.
 */
@RestController
@RequestMapping(path = "/api/reports/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAnalyticsController {

    private final IamRestClient iamRestClient;

    public UserAnalyticsController(IamRestClient iamRestClient) {
        this.iamRestClient = iamRestClient;
    }

    /**
     * GET /api/reports/users/analytics
     * Returns user counts grouped by status and role.
     */
    @GetMapping("/analytics")
    public UserAnalyticsRecord getUserAnalytics(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        List<UserDTO> users = iamRestClient.getAllUsersFromIam(authHeader);

        int total = users.size();
        int active = (int) users.stream().filter(u -> "ACTIVE".equalsIgnoreCase(u.status())).count();
        int inactive = (int) users.stream().filter(u -> "INACTIVE".equalsIgnoreCase(u.status())).count();
        int suspended = (int) users.stream().filter(u -> "SUSPENDED".equalsIgnoreCase(u.status())).count();

        // Count users per role
        Map<String, Integer> usersByRole = users.stream()
                .collect(Collectors.groupingBy(
                        u -> u.role() != null ? u.role() : "UNKNOWN",
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // Status breakdown per role: { "ADMIN": {"ACTIVE": 1, "INACTIVE": 0, ...}, ... }
        Map<String, Map<String, Integer>> statusByRole = new HashMap<>();
        for (UserDTO user : users) {
            String role = user.role() != null ? user.role() : "UNKNOWN";
            String status = user.status() != null ? user.status().toUpperCase() : "UNKNOWN";
            statusByRole
                    .computeIfAbsent(role, k -> new HashMap<>(Map.of("ACTIVE", 0, "INACTIVE", 0, "SUSPENDED", 0)))
                    .merge(status, 1, Integer::sum);
        }

        return new UserAnalyticsRecord(total, active, inactive, suspended, usersByRole, statusByRole, users);
    }

    /**
     * GET /api/reports/users/all
     * Returns the full list of users from IAM.
     */
    @GetMapping("/all")
    public List<UserDTO> getAllUsers(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return iamRestClient.getAllUsersFromIam(authHeader);
    }
}
