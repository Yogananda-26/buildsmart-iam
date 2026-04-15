package com.buildsmart.analytics.client;

/**
 * DTO for vendor data received from the Vendor Management microservice.
 */
public record VendorDTO(
        String vendorId,
        String vendorName,
        String category,
        String status,
        double rating,
        int activeContracts
) {
}
