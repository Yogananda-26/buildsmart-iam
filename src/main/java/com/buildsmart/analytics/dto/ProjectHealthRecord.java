package com.buildsmart.analytics.dto;

/**
 * Project health metrics including schedule variance and CPI.
 */
public record ProjectHealthRecord(double scheduleVariance, double costPerformanceIndex) {
}