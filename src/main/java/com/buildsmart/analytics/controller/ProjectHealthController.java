package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.ProjectHealthRecord;
import com.buildsmart.analytics.dto.ProjectSummaryRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/reports/project", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectHealthController {

    private final AnalyticsService analyticsService;

    public ProjectHealthController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "/{projectId}/health")
    public ProjectHealthRecord getProjectHealth(@PathVariable("projectId") String projectId) {
        return analyticsService.getProjectHealth(projectId);
    }

    @GetMapping(path = "/summary")
    public List<ProjectSummaryRecord> getActiveProjectSummary() {
        return analyticsService.getProjectSummary();
    }
}
