package com.buildsmart.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "resource-service", fallback = ResourceServiceFallback.class)
public interface ResourceServiceClient {

    @GetMapping("/api/resources/utilization")
    UtilizationDTO getUtilization();

    @GetMapping("/api/resources/labor")
    List<LaborDTO> getLaborAllocations();
}
