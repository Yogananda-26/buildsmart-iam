package com.buildsmart.analytics.dto;

import java.time.YearMonth;

/**
 * Cash flow summary for a tracked month.
 */
public record CashFlowRecord(YearMonth month, double invoices, double payments, double netOutflow) {
}