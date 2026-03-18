package com.buildsmart.iam.dto;

import com.buildsmart.iam.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response")
public class AuthResponse {
    
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT access token")
    private String token;
    
    @Schema(example = "Bearer", description = "Token type")
    private String type = "Bearer";
    
    @Schema(example = "3600", description = "Token expiration in seconds")
    private Long expiresIn;
    
    @Schema(example = "John Doe", description = "User's full name")
    private String name;
    
    @Schema(example = "john.doe@example.com", description = "User's email")
    private String email;
    
    @Schema(example = "PROJECT_MANAGER", description = "User's role")
    private Role role;
    
    @Schema(example = "1234567890", description = "User's phone number")
    private String phone;
    
    // Default constructor
    public AuthResponse() {}
    
    // Constructor with token and user info
    public AuthResponse(String token, String name, String email, String phone, Role role) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
