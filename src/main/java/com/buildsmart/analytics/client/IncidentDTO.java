package com.buildsmart.analytics.client;

import java.time.LocalDate;

public record IncidentDTO(
    LocalDate incidentDate,
    String severity,
    int incidentCount
) {}
