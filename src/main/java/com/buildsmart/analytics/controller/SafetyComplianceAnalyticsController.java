package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.SafetyInspectionSummaryRecord;
import com.buildsmart.analytics.dto.SafetyTrendRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for safety and compliance analytics.
 */
@RestController
@RequestMapping(path = "/api/reports/safety", produces = MediaType.APPLICATION_JSON_VALUE)
public class SafetyComplianceAnalyticsController {

    private final AnalyticsService analyticsService;

    public SafetyComplianceAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "/trends")
    public List<SafetyTrendRecord> getSafetyTrends() {
        return analyticsService.getSafetyTrends();
    }

    @GetMapping(path = "/inspections-summary")
    public SafetyInspectionSummaryRecord getInspectionsSummary() {
        return analyticsService.getSafetyInspectionsSummary();
    }
}
