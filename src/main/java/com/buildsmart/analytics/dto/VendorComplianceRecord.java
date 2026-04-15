package com.buildsmart.analytics.dto;

/**
 * Summary of vendor compliance across all active vendors.
 */
public record VendorComplianceRecord(
        int totalVendors,
        int compliantVendors,
        int nonCompliantVendors,
        int pendingReview,
        double complianceRate,
        int contractsExpiringSoon
) {
}
