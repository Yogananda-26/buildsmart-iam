package com.buildsmart.iam.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Validation error", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Request validation failed. Please check your input.",
                errors,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ApiResponse(responseCode = "400", description = "Bad request", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                null,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(
            InvalidTokenException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Token",
                ex.getMessage(),
                null,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    @ApiResponse(responseCode = "401", description = "Authentication error", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                "Invalid credentials or authentication token",
                null,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(responseCode = "403", description = "Access denied", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You don't have permission to access this resource",
                null,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Internal server error", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                null,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Error response class
    @Schema(description = "Standard error response format")
    public static class ErrorResponse {
        @Schema(example = "2024-01-15T10:30:00", description = "Timestamp of the error")
        private LocalDateTime timestamp;
        
        @Schema(example = "400", description = "HTTP status code")
        private int status;
        
        @Schema(example = "Validation Failed", description = "Error title")
        private String error;
        
        @Schema(example = "Request validation failed. Please check your input.", description = "Detailed error message")
        private String message;
        
        @Schema(description = "Field-specific validation errors")
        private Map<String, String> validationErrors;
        
        @Schema(example = "uri=/api/v1/auth/signup", description = "Request path")
        private String path;
        
        public ErrorResponse() {}
        
        public ErrorResponse(LocalDateTime timestamp, int status, String error, 
                            String message, Map<String, String> validationErrors, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.validationErrors = validationErrors;
            this.path = path;
        }
        
        // Getters and Setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}
