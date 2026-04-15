package com.buildsmart.analytics.client;

public record ProjectDTO(
        String projectId,
        String name,
        int plannedMilestones,
        int completedMilestones,
        String status
) {}