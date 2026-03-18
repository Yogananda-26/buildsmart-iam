package com.buildsmart.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User logout response")
public class LogoutResponse {

    @Schema(example = "true", description = "Indicates if logout was successful")
    private boolean success;

    @Schema(example = "Logout successful", description = "Logout status message")
    private String message;

    @Schema(example = "2024-01-15T10:30:00", description = "Time when the logout was processed")
    private LocalDateTime timestamp;

    public LogoutResponse() {
    }

    public LogoutResponse(boolean success, String message, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

