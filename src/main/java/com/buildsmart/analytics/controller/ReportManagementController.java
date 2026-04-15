package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.DashboardSummaryRecord;
import com.buildsmart.analytics.dto.GenerateReportRequest;
import com.buildsmart.analytics.dto.ReportResponseRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Primary report management endpoints for generation and retrieval.
 */
@RestController
@RequestMapping(path = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportManagementController {

    private final AnalyticsService analyticsService;

    public ReportManagementController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping(path = "/generate")
    public ReportResponseRecord generateReport(@Valid @RequestBody GenerateReportRequest request) {
        return analyticsService.generateReport(request);
    }

    @GetMapping(path = "/{id}")
    public ReportResponseRecord getReport(@PathVariable("id") String id) {
        return analyticsService.getReport(id);
    }

    @GetMapping(path = "/dashboard-summary")
    public DashboardSummaryRecord getDashboardSummary() {
        return analyticsService.getDashboardSummary();
    }
}
