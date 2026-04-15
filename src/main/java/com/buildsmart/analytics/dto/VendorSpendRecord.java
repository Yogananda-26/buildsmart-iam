package com.buildsmart.analytics.dto;

/**
 * Vendor spend analysis per category or period.
 */
public record VendorSpendRecord(
        String category,
        double budgeted,
        double actual,
        double variance,
        int vendorCount
) {
}
