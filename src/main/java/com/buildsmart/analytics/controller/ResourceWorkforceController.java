package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.LaborAllocationRecord;
import com.buildsmart.analytics.dto.ResourceUtilizationRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for resource and workforce analytics.
 */
@RestController
@RequestMapping(path = "/api/reports/resources", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourceWorkforceController {

    private final AnalyticsService analyticsService;

    public ResourceWorkforceController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "/utilization")
    public ResourceUtilizationRecord getResourceUtilization() {
        return analyticsService.getResourceUtilization();
    }

    @GetMapping(path = "/labor-allocation")
    public List<LaborAllocationRecord> getLaborAllocation() {
        return analyticsService.getLaborAllocation();
    }
}
