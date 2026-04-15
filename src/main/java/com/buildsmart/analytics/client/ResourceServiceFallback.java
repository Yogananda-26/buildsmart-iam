package com.buildsmart.analytics.client;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ResourceServiceFallback implements ResourceServiceClient {

    @Override
    public UtilizationDTO getUtilization() {
        return new UtilizationDTO(0.0, 0.0);
    }

    @Override
    public List<LaborDTO> getLaborAllocations() {
        return List.of();
    }
}
