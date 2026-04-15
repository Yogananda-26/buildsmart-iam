package com.buildsmart.analytics.dto;

/**
 * Performance summary for a site engineer over a period.
 */
public record SiteEngineerPerformanceRecord(
        String engineerId,
        String engineerName,
        String assignedProject,
        double taskCompletionRate,
        double avgHoursOnSite,
        int totalInspections,
        int issuesResolved,
        int issuesPending,
        double qualityScore,
        String performanceGrade
) {
}
