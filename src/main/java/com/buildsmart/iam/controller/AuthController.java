package com.buildsmart.iam.controller;

import com.buildsmart.iam.dto.AuthResponse;
import com.buildsmart.iam.dto.LoginRequest;
import com.buildsmart.iam.dto.LogoutRequest;
import com.buildsmart.iam.dto.LogoutResponse;
import com.buildsmart.iam.dto.SignupRequest;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.service.LogoutService;
import com.buildsmart.iam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private LogoutService logoutService;
    
    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email or phone already exists")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest,
                                         HttpServletRequest request) {
        try {
            User user = userService.registerUser(signupRequest, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "User registered successfully", 
                            new UserResponse(user.getUserId(), user.getName(), user.getEmail(), 
                                    user.getPhone(), user.getRole(), user.getStatus())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account not active")
    })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request) {
        try {
            AuthResponse authResponse = userService.authenticateUser(loginRequest, request);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CustomApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the current JWT token and records a logout audit event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Invalid or missing token"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LogoutResponse> logout(@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
                                                 @RequestBody(required = false) LogoutRequest logoutRequest,
                                                 HttpServletRequest request) {
        LogoutResponse response = logoutService.logout(authorizationHeader, request);
        return ResponseEntity.ok(response);
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
    
    public static class UserResponse {
        private String userId;
        private String name;
        private String email;
        private String phone;
        private com.buildsmart.iam.entity.Role role;
        private com.buildsmart.iam.entity.UserStatus status;
        
        public UserResponse(String userId, String name, String email, String phone, 
                          com.buildsmart.iam.entity.Role role, com.buildsmart.iam.entity.UserStatus status) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.role = role;
            this.status = status;
        }
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public com.buildsmart.iam.entity.Role getRole() { return role; }
        public void setRole(com.buildsmart.iam.entity.Role role) { this.role = role; }
        public com.buildsmart.iam.entity.UserStatus getStatus() { return status; }
        public void setStatus(com.buildsmart.iam.entity.UserStatus status) { this.status = status; }
    }
}
