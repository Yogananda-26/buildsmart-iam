package com.buildsmart.analytics.client;

public record LaborDTO(
    String siteName,
    double allocatedHours,
    double usedHours
) {}
