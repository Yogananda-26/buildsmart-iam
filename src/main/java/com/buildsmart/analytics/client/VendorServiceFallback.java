package com.buildsmart.analytics.client;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback for VendorServiceClient when the Vendor service is unavailable.
 * Returns realistic dummy data so the analytics module can still function.
 */
@Component
public class VendorServiceFallback implements VendorServiceClient {

    private static final Logger log = LoggerFactory.getLogger(VendorServiceFallback.class);

    @Override
    public List<VendorDTO> getAllVendors() {
        log.warn("Vendor service unavailable — returning fallback vendor list");
        return List.of(
                new VendorDTO("VND001", "Titan Steel Suppliers", "Materials", "ACTIVE", 4.5, 3),
                new VendorDTO("VND002", "QuickMix Concrete", "Materials", "ACTIVE", 4.2, 2),
                new VendorDTO("VND003", "ElectraPower Solutions", "Electrical", "ACTIVE", 4.8, 4),
                new VendorDTO("VND004", "SafeGuard Equipment", "Safety Equipment", "ACTIVE", 3.9, 1),
                new VendorDTO("VND005", "GreenScape Landscaping", "Landscaping", "INACTIVE", 3.5, 0),
                new VendorDTO("VND006", "PrimePlumb Services", "Plumbing", "ACTIVE", 4.1, 2),
                new VendorDTO("VND007", "AeroLift Crane Hire", "Heavy Equipment", "ACTIVE", 4.6, 3),
                new VendorDTO("VND008", "ClearView Glass Works", "Materials", "UNDER_REVIEW", 3.8, 1)
        );
    }

    @Override
    public VendorDTO getVendorById(String vendorId) {
        log.warn("Vendor service unavailable — returning fallback for vendorId: {}", vendorId);
        return new VendorDTO(vendorId, "Unknown Vendor", "Unknown", "ACTIVE", 0.0, 0);
    }

    @Override
    public List<VendorDTO> getActiveVendors() {
        log.warn("Vendor service unavailable — returning fallback active vendor list");
        return getAllVendors().stream().filter(v -> "ACTIVE".equals(v.status())).toList();
    }
}
