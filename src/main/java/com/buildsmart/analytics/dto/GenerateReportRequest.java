package com.buildsmart.analytics.dto;

import com.buildsmart.analytics.entity.Scope;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for generating a report asynchronously.
 */
public record GenerateReportRequest(
        @NotNull Scope scope,
        String targetId
) {
}