package com.buildsmart.analytics.dto;

/**
 * Resource utilization metrics showing equipment usage.
 */
public record ResourceUtilizationRecord(double usedHours, double idleHours, double utilizationRate) {
}