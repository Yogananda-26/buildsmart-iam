package com.buildsmart.iam.controller;

import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.service.UserService;
import com.buildsmart.iam.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtService jwtService;
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile", description = "Retrieves the current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> user = userService.findByEmail(email);
            
            if (user.isPresent()) {
                return ResponseEntity.ok(new CustomApiResponse(true, "Profile retrieved successfully", user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving profile: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update current user profile", description = "Updates the current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserProfileUpdateRequest updateRequest,
                                                      Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            
            // Update allowed fields (users cannot change their own role or status)
            if (updateRequest.getName() != null) {
                user.setName(updateRequest.getName());
            }
            if (updateRequest.getPhone() != null) {
                user.setPhone(updateRequest.getPhone());
            }
            
            User updatedUser = userService.updateUser(user.getUserId(), user);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Profile updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error updating profile: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/check-role/{requiredRole}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check user role", description = "Checks if current user has the specified role")
    public ResponseEntity<?> checkUserRole(@PathVariable String requiredRole, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> user = userService.findByEmail(email);
            
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            com.buildsmart.iam.entity.Role userRole = user.get().getRole();
            com.buildsmart.iam.entity.Role requiredRoleEnum = com.buildsmart.iam.entity.Role.valueOf(requiredRole.toUpperCase());
            
            boolean hasRole = userRole == requiredRoleEnum;
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Role check completed", 
                    new RoleCheckResponse(userRole.name(), hasRole)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse(false, "Invalid role: " + requiredRole, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error checking role: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by ID", description = "Fetches user details by userId (used for service-to-service lookup)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserLookupDto(
                    user.get().getUserId(),
                    user.get().getName(),
                    user.get().getEmail(),
                    user.get().getRole().name(),
                    user.get().getStatus().name()
            ));
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/by-email")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by email", description = "Fetches user details by email (used for service-to-service lookup)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserLookupDto(
                    user.get().getUserId(),
                    user.get().getName(),
                    user.get().getEmail(),
                    user.get().getRole().name(),
                    user.get().getStatus().name()
            ));
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Get all users (Project Manager only)", description = "Retrieves all users in the system (Project Manager only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> getAllUsersForProjectManager() {
        try {
            java.util.List<User> users = userService.findAllUsers();
            return ResponseEntity.ok(new CustomApiResponse(true, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving users: " + e.getMessage(), null));
        }
    }
    
    // Helper classes
    
    /**
     * Lightweight DTO for service-to-service user lookup.
     * Does not expose password or audit fields.
     */
    public record UserLookupDto(
            String userId,
            String name,
            String email,
            String role,
            String status
    ) {}
    
    public static class CustomApiResponse {
        private boolean success;
        private String message;
        private Object data;
        
        public CustomApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
    
    public static class UserProfileUpdateRequest {
        private String name;
        @jakarta.validation.constraints.Pattern(regexp = "^[A-Za-z0-9+_.-]+@gmail\\.com$", message = "Email must be a valid Gmail address (e.g., user@gmail.com)")
        private String email;
        private String phone;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
    
    public static class RoleCheckResponse {
        private String currentRole;
        private boolean hasRequiredRole;
        
        public RoleCheckResponse(String currentRole, boolean hasRequiredRole) {
            this.currentRole = currentRole;
            this.hasRequiredRole = hasRequiredRole;
        }
        
        // Getters and Setters
        public String getCurrentRole() { return currentRole; }
        public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }
        public boolean isHasRequiredRole() { return hasRequiredRole; }
        public void setHasRequiredRole(boolean hasRequiredRole) { this.hasRequiredRole = hasRequiredRole; }
    }
}
