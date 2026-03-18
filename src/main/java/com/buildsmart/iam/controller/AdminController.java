package com.buildsmart.iam.controller;

import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.AuditLog;
import com.buildsmart.iam.service.UserService;
import com.buildsmart.iam.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Management", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending users", description = "Retrieves users awaiting approval (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> getPendingUsers() {
        try {
            List<User> pendingUsers = userService.findByStatus(com.buildsmart.iam.entity.UserStatus.PENDING_VERIFICATION);
            
            CustomApiResponse response = new CustomApiResponse(true, "Pending users retrieved successfully", pendingUsers);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomApiResponse response = new CustomApiResponse(false, "Error retrieving pending users: " + e.getMessage(), null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/approve-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve user", description = "Approves a pending user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User approved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> approveUser(@PathVariable String userId, HttpServletRequest request) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                CustomApiResponse response = new CustomApiResponse(false, "User not found", null);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            User user = userOpt.get();
            if (user.getStatus() != com.buildsmart.iam.entity.UserStatus.PENDING_VERIFICATION) {
                CustomApiResponse response = new CustomApiResponse(false, "User is not pending verification", null);
                
                return ResponseEntity.badRequest().body(response);
            }
            
            user.setStatus(com.buildsmart.iam.entity.UserStatus.ACTIVE);
            User updatedUser = userService.updateUser(userId, user);
            
            auditService.logAction(userId, "USER_APPROVED", "User Management", 
                    "User approved by admin: " + updatedUser.getEmail(), request);
            
            CustomApiResponse response = new CustomApiResponse(true, "User approved successfully", updatedUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomApiResponse response = new CustomApiResponse(false, "Error approving user: " + e.getMessage(), null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/reject-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject user", description = "Rejects a pending user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User rejected successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> rejectUser(@PathVariable String userId, HttpServletRequest request) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                CustomApiResponse response = new CustomApiResponse(false, "User not found", null);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            User user = userOpt.get();
            if (user.getStatus() != com.buildsmart.iam.entity.UserStatus.PENDING_VERIFICATION) {
                CustomApiResponse response = new CustomApiResponse(false, "User is not pending verification", null);
                
                return ResponseEntity.badRequest().body(response);
            }
            
            user.setStatus(com.buildsmart.iam.entity.UserStatus.INACTIVE);
            User updatedUser = userService.updateUser(userId, user);
            
            auditService.logAction(userId, "USER_REJECTED", "User Management", 
                    "User rejected by admin: " + updatedUser.getEmail(), request);
            
            CustomApiResponse response = new CustomApiResponse(true, "User rejected successfully", updatedUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomApiResponse response = new CustomApiResponse(false, "Error rejecting user: " + e.getMessage(), null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves all users in the system (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {
        
        try {
            // Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            //         Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            // Pageable pageable = PageRequest.of(page, size, sort);
            
            // For now, we'll use findAllUsers() since we don't have pagination in repository
            List<User> users = userService.findAllUsers();
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error retrieving users: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> getUserById(@PathVariable String userId, HttpServletRequest request) {
        try {
            Optional<User> user = userService.findById(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok(new CustomApiResponse(true, "User retrieved successfully", user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error retrieving user: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Updates user information (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> updateUser(@PathVariable String userId, 
                                       @Valid @RequestBody UserUpdateRequest updateRequest,
                                       HttpServletRequest request) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            user.setName(updateRequest.getName());
            user.setPhone(updateRequest.getPhone());
            user.setRole(updateRequest.getRole());
            user.setStatus(updateRequest.getStatus());
            
            User updatedUser = userService.updateUser(userId, user);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error updating user: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes a user from the system (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> deleteUser(@PathVariable String userId, HttpServletRequest request) {
        try {
            Optional<User> user = userService.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            userService.deleteUser(userId);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error deleting user: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Retrieves users by their role (Admin only)")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            com.buildsmart.iam.entity.Role userRole = com.buildsmart.iam.entity.Role.valueOf(role.toUpperCase());
            List<User> users = userService.findByRole(userRole);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Users retrieved successfully", users));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse(false, "Invalid role: " + role, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error retrieving users: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs", description = "Retrieves system audit logs (Admin only)")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<AuditLog> auditLogs = auditService.getAllAuditLogs(pageable);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/audit-logs/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs for user", description = "Retrieves audit logs for a specific user (Admin only)")
    public ResponseEntity<?> getAuditLogsByUser(@PathVariable String userId) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByUserId(userId);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
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
    
    public static class UserUpdateRequest {
        private String name;
        private String phone;
        private com.buildsmart.iam.entity.Role role;
        private com.buildsmart.iam.entity.UserStatus status;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public com.buildsmart.iam.entity.Role getRole() { return role; }
        public void setRole(com.buildsmart.iam.entity.Role role) { this.role = role; }
        public com.buildsmart.iam.entity.UserStatus getStatus() { return status; }
        public void setStatus(com.buildsmart.iam.entity.UserStatus status) { this.status = status; }
    }
}
