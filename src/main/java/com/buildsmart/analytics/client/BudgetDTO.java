package com.buildsmart.analytics.client;

public record BudgetDTO(
    String projectId,
    double plannedBudget,
    double actualSpent
) {}
