package com.buildsmart.analytics.dto;

import com.buildsmart.analytics.entity.Scope;
import java.time.OffsetDateTime;

/**
 * A simplified view of reports stored for history retrieval.
 */
public record HistoricalReportRecord(String reportId, Scope scope, OffsetDateTime generatedDate) {
}