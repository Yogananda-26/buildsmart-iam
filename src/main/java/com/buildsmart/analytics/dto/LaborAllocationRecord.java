package com.buildsmart.analytics.dto;

/**
 * Labor allocation details grouped by project site.
 */
public record LaborAllocationRecord(String site, double allocatedHours, double availableHours) {
}