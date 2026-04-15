package com.buildsmart.analytics.controller;

import com.buildsmart.analytics.dto.BudgetAlertRecord;
import com.buildsmart.analytics.dto.CashFlowRecord;
import com.buildsmart.analytics.service.AnalyticsService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/reports/finance", produces = MediaType.APPLICATION_JSON_VALUE)
public class FinancialBudgetController {

    private final AnalyticsService analyticsService;

    public FinancialBudgetController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "/budget-variance/{projectId}")
    public BudgetAlertRecord getBudgetVariance(@PathVariable("projectId") String projectId) {
        return analyticsService.getBudgetVariance(projectId);
    }

    @GetMapping(path = "/cash-flow")
    public List<CashFlowRecord> getCashFlow() {
        return analyticsService.getCashFlow();
    }
}
