package com.buildsmart.analytics.dto;

/**
 * Project overview item for active project dashboards.
 */
public record ProjectSummaryRecord(
        String projectId,
        String projectName,
        double progressPercent,
        double budgetVariancePercent,
        String status
) {
}