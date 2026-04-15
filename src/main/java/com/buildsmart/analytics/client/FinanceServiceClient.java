package com.buildsmart.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "finance-service", fallback = FinanceServiceFallback.class)
public interface FinanceServiceClient {

    @GetMapping("/api/finance/budgets/{projectId}")
    BudgetDTO getBudget(@PathVariable String projectId);

    @GetMapping("/api/finance/cashflow")
    List<CashFlowDTO> getCashFlow();
}
