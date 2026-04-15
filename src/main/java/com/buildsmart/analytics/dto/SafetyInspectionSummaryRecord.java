package com.buildsmart.analytics.dto;

/**
 * Summary of site inspections showing pass and fail ratios.
 */
public record SafetyInspectionSummaryRecord(long passed, long failed, double passRatio) {
}