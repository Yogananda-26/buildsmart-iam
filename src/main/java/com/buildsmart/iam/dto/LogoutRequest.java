package com.buildsmart.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Logout request payload.
 * 
 * Currently the logout flow relies primarily on the Authorization header
 * for the JWT token, but this DTO is kept for future extensibility
 * (for example device identifiers or logout reasons).
 */
@Schema(description = "User logout request")
public class LogoutRequest {

    @Schema(description = "Optional client identifier or device information", example = "web-client")
    private String clientId;

    public LogoutRequest() {
    }

    public LogoutRequest(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

