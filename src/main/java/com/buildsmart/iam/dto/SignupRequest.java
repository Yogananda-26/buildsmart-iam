package com.buildsmart.iam.dto;

import com.buildsmart.iam.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "User signup request")
public class SignupRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(example = "John Doe", description = "User's full name")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(example = "john.doe@example.com", description = "User's email address")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    @Schema(example = "+1234567890", description = "User's phone number")
    private String phone;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    @Schema(example = "Password123", description = "User's password")
    private String password;
    
    @Schema(example = "PROJECT_MANAGER", description = "User's role in the system")
    private Role role;
    
    // Default constructor
    public SignupRequest() {}
    
    // Constructor with required fields
    public SignupRequest(String name, String email, String phone, String password, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}
