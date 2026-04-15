package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.ExportStatusRecord;
import com.buildsmart.analytics.dto.HistoricalReportRecord;
import com.buildsmart.analytics.entity.Scope;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class HistoricalExportController {

    private final AnalyticsService analyticsService;

    public HistoricalExportController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "/history/{scope}")
    public List<HistoricalReportRecord> getHistoricalReports(@PathVariable("scope") Scope scope) {
        return analyticsService.getHistoricalReports(scope);
    }

    @PostMapping(path = "/export/{reportId}")
    public ExportStatusRecord exportReport(@PathVariable("reportId") String reportId) {
        return analyticsService.exportReport(reportId);
    }
}
