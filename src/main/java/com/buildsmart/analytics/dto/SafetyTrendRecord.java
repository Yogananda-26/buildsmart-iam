package com.buildsmart.analytics.dto;

import java.time.LocalDate;

public record SafetyTrendRecord(LocalDate date, String severityCategory, long incidentCount) {
}