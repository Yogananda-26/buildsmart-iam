package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.VendorComplianceRecord;
import com.buildsmart.analytics.dto.VendorPerformanceRecord;
import com.buildsmart.analytics.dto.VendorSpendRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for vendor analytics and reporting.
 */
@RestController
@RequestMapping(path = "/api/reports/vendor", produces = MediaType.APPLICATION_JSON_VALUE)
public class VendorAnalyticsController {

    private final AnalyticsService analyticsService;

    public VendorAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get performance metrics for all vendors.
     */
    @GetMapping(path = "/performance")
    public List<VendorPerformanceRecord> getVendorPerformance() {
        return analyticsService.getVendorPerformance();
    }

    /**
     * Get performance metrics for a specific vendor.
     */
    @GetMapping(path = "/performance/{vendorId}")
    public VendorPerformanceRecord getVendorPerformanceById(@PathVariable("vendorId") String vendorId) {
        return analyticsService.getVendorPerformanceById(vendorId);
    }

    /**
     * Get vendor compliance summary across all vendors.
     */
    @GetMapping(path = "/compliance")
    public VendorComplianceRecord getVendorCompliance() {
        return analyticsService.getVendorCompliance();
    }

    /**
     * Get vendor spend analysis by category.
     */
    @GetMapping(path = "/spend")
    public List<VendorSpendRecord> getVendorSpend() {
        return analyticsService.getVendorSpend();
    }
}
