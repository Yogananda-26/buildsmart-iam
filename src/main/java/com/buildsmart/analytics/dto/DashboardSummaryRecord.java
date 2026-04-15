package com.buildsmart.analytics.dto;

/**
 * Aggregated dashboard summary metrics for project managers.
 */
public record DashboardSummaryRecord(
        long activeProjects,
        double averageBudgetVariance,
        double safetyComplianceRate,
        double resourceUtilizationRate
) {
}