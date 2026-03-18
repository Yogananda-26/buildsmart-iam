package com.buildsmart.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Forgot password request")
public class ForgotPasswordRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(example = "john.doe@example.com", description = "User's email address")
    private String email;
    
    // Default constructor
    public ForgotPasswordRequest() {}
    
    // Constructor with email
    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
