package com.buildsmart.analytics.dto;

/**
 * Represents a single vendor's performance metrics.
 */
public record VendorPerformanceRecord(
        String vendorId,
        String vendorName,
        double onTimeDeliveryRate,
        double qualityScore,
        double costVariance,
        int activeContracts,
        String overallRating
) {
}
