package com.buildsmart.iam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Reset password request")
public class ResetPasswordRequest {
    
    @NotBlank(message = "Token is required")
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "Password reset token")
    private String token;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    @Schema(example = "NewPassword123", description = "New password")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    @Schema(example = "NewPassword123", description = "Confirm new password")
    private String confirmPassword;
    
    // Default constructor
    public ResetPasswordRequest() {}
    
    // Constructor with fields
    public ResetPasswordRequest(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
