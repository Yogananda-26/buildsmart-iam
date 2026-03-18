package com.buildsmart.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login request")
public class LoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(example = "john.doe@example.com", description = "User's email address")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Schema(example = "Password123", description = "User's password")
    private String password;
    
    // Default constructor
    public LoginRequest() {}
    
    // Constructor with fields
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
