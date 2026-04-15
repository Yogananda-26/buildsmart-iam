package com.buildsmart.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "safety-service", fallback = SafetyServiceFallback.class)
public interface SafetyServiceClient {

    @GetMapping("/api/safety/incidents")
    List<IncidentDTO> getIncidents();

    @GetMapping("/api/safety/inspections/summary")
    InspectionSummaryDTO getInspectionSummary();
}