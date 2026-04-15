package com.buildsmart.analytics.client;

/**
 * DTO for site engineer data received from the Site Engineer microservice.
 */
public record SiteEngineerDTO(
        String engineerId,
        String engineerName,
        String assignedProjectId,
        String assignedProjectName,
        String status,
        String specialization
) {
}
