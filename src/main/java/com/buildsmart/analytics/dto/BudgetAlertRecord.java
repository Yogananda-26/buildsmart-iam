package com.buildsmart.analytics.dto;

/**
 * Alert record for budget variance thresholds.
 */
public record BudgetAlertRecord(
        String projectId,
        double plannedAmount,
        double actualAmount,
        double variance,
        boolean thresholdExceeded
) {
}