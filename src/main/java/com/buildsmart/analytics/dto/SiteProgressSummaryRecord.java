package com.buildsmart.analytics.dto;

/**
 * Aggregated site progress summary across all site engineers.
 */
public record SiteProgressSummaryRecord(
        int totalSiteEngineers,
        int activeSites,
        double avgTaskCompletionRate,
        double avgQualityScore,
        int totalIssuesOpen,
        int totalIssuesResolved,
        int inspectionsThisMonth,
        double siteEfficiencyIndex
) {
}
