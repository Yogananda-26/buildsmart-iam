package com.buildsmart.analytics.dto;

/**
 * Daily log entry for a site engineer — tracks site activities and progress.
 */
public record SiteEngineerDailyLogRecord(
        String logId,
        String engineerId,
        String engineerName,
        String projectId,
        String projectName,
        String date,
        double hoursOnSite,
        int tasksCompleted,
        int tasksAssigned,
        int issuesReported,
        String weatherCondition,
        String remarks
) {
}
