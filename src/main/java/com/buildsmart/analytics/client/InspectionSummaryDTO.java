package com.buildsmart.analytics.client;

public record InspectionSummaryDTO(
    long passed,
    long failed,
    double complianceScore
) {}
