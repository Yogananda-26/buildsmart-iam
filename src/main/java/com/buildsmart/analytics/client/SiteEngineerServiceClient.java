package com.buildsmart.analytics.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to communicate with the Site Engineer microservice.
 */
@FeignClient(
        name = "site-engineer-service",
        fallback = SiteEngineerServiceFallback.class
)
public interface SiteEngineerServiceClient {

    @GetMapping("/api/site-engineers")
    List<SiteEngineerDTO> getAllSiteEngineers();

    @GetMapping("/api/site-engineers/{engineerId}")
    SiteEngineerDTO getSiteEngineerById(@PathVariable("engineerId") String engineerId);

    @GetMapping("/api/site-engineers/active")
    List<SiteEngineerDTO> getActiveSiteEngineers();
}
