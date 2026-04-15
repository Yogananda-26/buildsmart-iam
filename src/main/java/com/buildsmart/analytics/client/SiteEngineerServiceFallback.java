package com.buildsmart.analytics.client;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback for SiteEngineerServiceClient when the service is unavailable.
 */
@Component
public class SiteEngineerServiceFallback implements SiteEngineerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(SiteEngineerServiceFallback.class);

    @Override
    public List<SiteEngineerDTO> getAllSiteEngineers() {
        log.warn("Site Engineer service unavailable — returning fallback data");
        return List.of(
                new SiteEngineerDTO("SE001", "Rajesh Kumar", "P-1001", "West Park Towers", "ACTIVE", "Structural"),
                new SiteEngineerDTO("SE002", "Anil Sharma", "P-1002", "Lakeview Residences", "ACTIVE", "Civil"),
                new SiteEngineerDTO("SE003", "Priya Patel", "P-1003", "Steel Bridge Crossing", "ACTIVE", "Geotechnical"),
                new SiteEngineerDTO("SE004", "Vikram Singh", "P-1001", "West Park Towers", "ACTIVE", "Electrical"),
                new SiteEngineerDTO("SE005", "Meena Reddy", "P-1002", "Lakeview Residences", "ON_LEAVE", "Structural"),
                new SiteEngineerDTO("SE006", "Suresh Nair", "P-1003", "Steel Bridge Crossing", "ACTIVE", "Mechanical")
        );
    }

    @Override
    public SiteEngineerDTO getSiteEngineerById(String engineerId) {
        log.warn("Site Engineer service unavailable — fallback for engineerId: {}", engineerId);
        return new SiteEngineerDTO(engineerId, "Unknown Engineer", "UNKNOWN", "Unknown Project", "ACTIVE", "General");
    }

    @Override
    public List<SiteEngineerDTO> getActiveSiteEngineers() {
        log.warn("Site Engineer service unavailable — returning fallback active engineers");
        return getAllSiteEngineers().stream().filter(e -> "ACTIVE".equals(e.status())).toList();
    }
}
