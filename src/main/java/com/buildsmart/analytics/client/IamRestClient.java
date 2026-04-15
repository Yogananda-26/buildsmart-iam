package com.buildsmart.analytics.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Direct HTTP client for IAM service — uses Java HttpClient to bypass all Spring complexity
 * and call IAM's /api/v1/admin/users endpoint reliably.
 */
@Service
public class IamRestClient {

    private static final Logger log = LoggerFactory.getLogger(IamRestClient.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${iam.service.url:http://localhost:8083}")
    private String iamBaseUrl;
    
    @Value("${iam.username:admin}")
    private String iamUsername;
    
    @Value("${iam.password:admin}")
    private String iamPassword;

    /**
     * Fetches all users from IAM's admin endpoint.
     * Uses the provided Authorization header or tries to extract from request context.
     *
     * @param authHeader the Authorization header to forward (can be null)
     * @return list of UserDTO from IAM, or empty list if IAM is unavailable
     */
    public List<UserDTO> getAllUsersFromIam(String authHeader) {
        // Ensure iamBaseUrl is set, fallback to default if not
        String effectiveUrl = iamBaseUrl != null && !iamBaseUrl.isEmpty() ? iamBaseUrl : "http://localhost:8083";
        String url = effectiveUrl + "/admin/users";
        log.info("[IAM DEBUG] Calling IAM at: {} (baseUrl={})", url, effectiveUrl);

        try {
            // Use provided auth header or try to extract from request context
            String authToUse = authHeader != null ? authHeader : getAuthorizationHeader();
            log.info("[IAM DEBUG] Authorization header to use: {}", authToUse);

            // If no auth header from request, use basic auth with configured credentials
            if (authToUse == null) {
                authToUse = createBasicAuthHeader(iamUsername, iamPassword);
                log.info("[IAM DEBUG] Using Basic Auth for IAM call with username: {}", iamUsername);
            } else {
                log.info("[IAM DEBUG] Forwarding Authorization header to IAM: {}...", authToUse.substring(0, Math.min(30, authToUse.length())));
            }

            // Build HTTP request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json");

            if (authToUse != null) {
                requestBuilder.header("Authorization", authToUse);
            }

            HttpRequest request = requestBuilder.build();
            log.info("[IAM DEBUG] Sending HTTP GET to: {}", url);

            // Execute request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("[IAM DEBUG] IAM response status: {}", response.statusCode());
            log.info("[IAM DEBUG] IAM response headers: {}", response.headers());
            if (response.body() != null) {
                log.info("[IAM DEBUG] IAM response body length: {}", response.body().length());
                log.info("[IAM DEBUG] IAM response body (first 500 chars): {}", response.body().substring(0, Math.min(500, response.body().length())));
            }

            if (response.statusCode() == 200 && response.body() != null && !response.body().isEmpty()) {
                try {
                    // Parse IAM response: { "success": true, "message": "...", "data": [...] }
                    JsonNode root = objectMapper.readTree(response.body());
                    boolean success = root.path("success").asBoolean(false);
                    log.info("[IAM DEBUG] IAM success field: {}", success);

                    if (success && root.has("data")) {
                        JsonNode dataNode = root.get("data");
                        log.info("[IAM DEBUG] IAM data field type: {}, is array: {}", dataNode.getNodeType(), dataNode.isArray());
                        log.info("[IAM DEBUG] IAM data field content (first 500 chars): {}", dataNode.toString().substring(0, Math.min(500, dataNode.toString().length())));

                        List<IamUserDTO> iamUsers = objectMapper.readValue(
                                dataNode.toString(),
                                new TypeReference<List<IamUserDTO>>() {}
                        );
                        log.info("[IAM DEBUG] Successfully fetched {} users from IAM", iamUsers.size());
                        return iamUsers.stream()
                                .map(u -> new UserDTO(u.userId(), u.name(), u.email(), u.role(), u.status()))
                                .toList();
                    } else {
                        log.warn("[IAM DEBUG] IAM returned success={} with data field present={}", success, root.has("data"));
                    }
                } catch (Exception parseEx) {
                    log.error("[IAM DEBUG] Failed to parse IAM response: {}", parseEx.getMessage(), parseEx);
                }
            } else {
                log.warn("[IAM DEBUG] IAM returned unexpected status {}: {}", response.statusCode(), response.body() != null ? response.body().substring(0, Math.min(100, response.body().length())) : "null");
            }
        } catch (IOException e) {
            log.error("[IAM DEBUG] IO error calling IAM at {}: {}", url, e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error("[IAM DEBUG] Interrupted while calling IAM at {}: {}", url, e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("[IAM DEBUG] Unexpected error calling IAM at {}: {} - {}", url, e.getClass().getSimpleName(), e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    /**
     * Creates a Basic Auth header value from username and password
     */
    private String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    /**
     * Extracts the Authorization header from the current HTTP request.
     */
    private String getAuthorizationHeader() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                return request.getHeader("Authorization");
            }
        } catch (Exception e) {
            log.debug("Could not get Authorization header: {}", e.getMessage());
        }
        return null;
    }
}

