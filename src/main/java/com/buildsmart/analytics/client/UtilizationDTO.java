package com.buildsmart.analytics.client;

public record UtilizationDTO(
    double usedHours,
    double idleHours
) {}
