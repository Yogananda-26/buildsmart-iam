package com.buildsmart.analytics.client;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class SafetyServiceFallback implements SafetyServiceClient {

    @Override
    public List<IncidentDTO> getIncidents() {
        // Return empty list when Safety service is down
        return List.of();
    }

    @Override
    public InspectionSummaryDTO getInspectionSummary() {
        return new InspectionSummaryDTO(0, 0, 0.0);
    }
}
