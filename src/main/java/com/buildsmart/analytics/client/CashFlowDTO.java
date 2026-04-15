package com.buildsmart.analytics.client;

import java.time.YearMonth;

public record CashFlowDTO(
    YearMonth month,
    double inflow,
    double outflow
) {}
