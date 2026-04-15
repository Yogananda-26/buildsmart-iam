package com.buildsmart.analytics.dto;

import com.buildsmart.analytics.entity.Scope;
import java.time.OffsetDateTime;

/**
 * Response record returned when a report is generated or fetched.
 */
public record ReportResponseRecord(String reportId, Scope scope, String metrics, OffsetDateTime generatedDate) {
}