package com.buildsmart.analytics.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to communicate with the Vendor Management microservice.
 */
@FeignClient(
        name = "vendor-service",
        fallback = VendorServiceFallback.class
)
public interface VendorServiceClient {

    @GetMapping("/api/vendors")
    List<VendorDTO> getAllVendors();

    @GetMapping("/api/vendors/{vendorId}")
    VendorDTO getVendorById(@PathVariable("vendorId") String vendorId);

    @GetMapping("/api/vendors/active")
    List<VendorDTO> getActiveVendors();
}
