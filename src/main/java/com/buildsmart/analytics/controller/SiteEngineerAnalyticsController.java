package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.SiteEngineerDailyLogRecord;
import com.buildsmart.analytics.dto.SiteEngineerPerformanceRecord;
import com.buildsmart.analytics.dto.SiteProgressSummaryRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for site engineer analytics and reporting.
 */
@RestController
@RequestMapping(path = "/api/reports/site-engineer", produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteEngineerAnalyticsController {

    private final AnalyticsService analyticsService;

    public SiteEngineerAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get performance metrics for all site engineers.
     */
    @GetMapping(path = "/performance")
    public List<SiteEngineerPerformanceRecord> getSiteEngineerPerformance() {
        return analyticsService.getSiteEngineerPerformance();
    }

    /**
     * Get performance for a specific site engineer.
     */
    @GetMapping(path = "/performance/{engineerId}")
    public SiteEngineerPerformanceRecord getSiteEngineerPerformanceById(
            @PathVariable("engineerId") String engineerId) {
        return analyticsService.getSiteEngineerPerformanceById(engineerId);
    }

    /**
     * Get aggregated site progress summary.
     */
    @GetMapping(path = "/summary")
    public SiteProgressSummaryRecord getSiteProgressSummary() {
        return analyticsService.getSiteProgressSummary();
    }

    /**
     * Get daily logs for all site engineers.
     */
    @GetMapping(path = "/daily-logs")
    public List<SiteEngineerDailyLogRecord> getDailyLogs() {
        return analyticsService.getSiteEngineerDailyLogs();
    }

    /**
     * Get daily logs for a specific site engineer.
     */
    @GetMapping(path = "/daily-logs/{engineerId}")
    public List<SiteEngineerDailyLogRecord> getDailyLogsByEngineer(
            @PathVariable("engineerId") String engineerId) {
        return analyticsService.getSiteEngineerDailyLogsByEngineer(engineerId);
    }
}
