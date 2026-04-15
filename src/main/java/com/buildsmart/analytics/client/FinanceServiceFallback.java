package com.buildsmart.analytics.client;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FinanceServiceFallback implements FinanceServiceClient {

    @Override
    public BudgetDTO getBudget(String projectId) {
        return new BudgetDTO(projectId, 0.0, 0.0);
    }

    @Override
    public List<CashFlowDTO> getCashFlow() {
        return List.of();
    }
}
