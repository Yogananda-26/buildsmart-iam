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
@CrossOrigin(origins = "*", maxAge = 3600)
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
    
    // Helper classes
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
        private String phone;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
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
