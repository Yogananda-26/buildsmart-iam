package com.buildsmart.iam.controller;

import com.buildsmart.iam.entity.AuditLog;
import com.buildsmart.iam.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/audit")
@Tag(name = "Audit Management", description = "Audit log management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditController {
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all audit logs", description = "Retrieves all audit logs with pagination (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<AuditLog> auditLogs = auditService.getAllAuditLogs(pageable);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/logs/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by user", description = "Retrieves audit logs for a specific user (Admin only)")
    public ResponseEntity<?> getAuditLogsByUser(@PathVariable String userId) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByUserId(userId);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/logs/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by action", description = "Retrieves audit logs for a specific action (Admin only)")
    public ResponseEntity<?> getAuditLogsByAction(@PathVariable String action) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByAction(action);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/logs/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by date range", description = "Retrieves audit logs within a date range (Admin only)")
    public ResponseEntity<?> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByDateRange(start, end);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/logs/user/{userId}/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by user and action", description = "Retrieves audit logs for a specific user and action (Admin only)")
    public ResponseEntity<?> getAuditLogsByUserAndAction(@PathVariable String userId, @PathVariable String action) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByUserAndAction(userId, action);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Audit logs retrieved successfully", auditLogs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving audit logs: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/stats/action-count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get action count since", description = "Counts occurrences of a specific action since a given time (Admin only)")
    public ResponseEntity<?> getActionCountSince(
            @RequestParam String action,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        
        try {
            long count = auditService.getActionCountSince(action, since);
            
            return ResponseEntity.ok(new CustomApiResponse(true, "Action count retrieved successfully", 
                    new ActionCountResponse(action, since, count)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CustomApiResponse(false, "Error retrieving action count: " + e.getMessage(), null));
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
    
    public static class ActionCountResponse {
        private String action;
        private LocalDateTime since;
        private long count;
        
        public ActionCountResponse(String action, LocalDateTime since, long count) {
            this.action = action;
            this.since = since;
            this.count = count;
        }
        
        // Getters and Setters
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public LocalDateTime getSince() { return since; }
        public void setSince(LocalDateTime since) { this.since = since; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}
