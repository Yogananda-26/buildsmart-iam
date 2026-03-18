package com.buildsmart.iam.controller;

import com.buildsmart.iam.dto.ForgotPasswordRequest;
import com.buildsmart.iam.dto.ResetPasswordRequest;
import com.buildsmart.iam.service.PasswordResetService;
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
@Tag(name = "Password Management", description = "Password management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PasswordController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Sends a password reset link to the user's email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            passwordResetService.forgotPassword(request, httpRequest);
            
            return ResponseEntity.ok(new CustomApiResponse(true, 
                    "If an account exists with this email, a password reset link has been sent.", 
                    null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets the user's password using the reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token or passwords don't match")
    })
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                         HttpServletRequest httpRequest) {
        try {
            passwordResetService.resetPassword(request, httpRequest);
            
            return ResponseEntity.ok(new CustomApiResponse(true, 
                    "Password has been reset successfully. You can now login with your new password.", 
                    null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/validate-reset-token/{token}")
    @Operation(summary = "Validate reset token", description = "Validates if a password reset token is still valid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token validation result"),
        @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    public ResponseEntity<?> validateResetToken(@PathVariable String token) {
        try {
            boolean isValid = passwordResetService.validateResetToken(token);
            
            return ResponseEntity.ok(new CustomApiResponse(true, 
                    isValid ? "Token is valid" : "Token is invalid or expired", 
                    new TokenValidationResponse(isValid)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Error validating token: " + e.getMessage(), null));
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
    
    public static class TokenValidationResponse {
        private boolean valid;
        
        public TokenValidationResponse(boolean valid) {
            this.valid = valid;
        }
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
    }
}
