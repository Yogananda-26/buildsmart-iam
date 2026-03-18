package com.buildsmart.iam.audit;

import com.buildsmart.iam.entity.AuditLog;
import com.buildsmart.iam.security.JwtService;
import com.buildsmart.iam.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuditInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private JwtService jwtService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Store request start time for performance logging
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            // Calculate request duration
            long startTime = (Long) request.getAttribute("startTime");
            long duration = System.currentTimeMillis() - startTime;
            
            // Get user ID from JWT token if available
            String userId = null;
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    userId = jwtService.extractUserId(token);
                } catch (Exception e) {
                    // Token is invalid, skip audit logging
                    return;
                }
            }
            
            // Determine action based on HTTP method and URI
            String action = determineAction(request.getMethod(), request.getRequestURI());
            
            // Log important actions
            if (shouldLogAction(request.getRequestURI(), request.getMethod())) {
                String details = buildAuditDetails(request, response, duration, ex);
                
                auditService.logAction(userId, action, 
                        request.getRequestURI(), details, request);
            }
        } catch (Exception e) {
            // Don't let audit logging interfere with the request
            System.err.println("Error in audit logging: " + e.getMessage());
        }
    }
    
    private String determineAction(String method, String uri) {
        String action = method;
        
        if (uri.contains("/auth/login")) {
            action = "LOGIN_ATTEMPT";
        } else if (uri.contains("/auth/signup")) {
            action = "USER_REGISTRATION";
        } else if (uri.contains("/auth/forgot-password")) {
            action = "PASSWORD_RESET_REQUESTED";
        } else if (uri.contains("/auth/reset-password")) {
            action = "PASSWORD_RESET_COMPLETED";
        } else if (uri.contains("/admin/users") && method.equals("GET")) {
            action = "USERS_LIST_ACCESSED";
        } else if (uri.contains("/admin/users") && method.equals("DELETE")) {
            action = "USER_DELETED";
        } else if (uri.contains("/admin/audit-logs")) {
            action = "AUDIT_LOGS_ACCESSED";
        } else if (uri.contains("/users/profile")) {
            action = "PROFILE_ACCESSED";
        }
        
        return action;
    }
    
    private boolean shouldLogAction(String uri, String method) {
        // Skip Swagger and documentation paths
        if (uri.contains("swagger-ui") || uri.contains("api-docs") || uri.equals("/swagger-ui.html")) {
            return false;
        }
        
        // Skip authentication paths from audit logging to avoid noise
        if (uri.contains("/auth/")) {
            return false;
        }
        
        // Log admin actions
        if (uri.contains("/admin/")) {
            return true;
        }
        
        // Log sensitive user actions
        if (uri.contains("/users/profile") && method.equals("PUT")) {
            return true;
        }
        
        return false;
    }
    
    private String buildAuditDetails(HttpServletRequest request, HttpServletResponse response, 
                                   long duration, Exception ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("method", request.getMethod());
        details.put("uri", request.getRequestURI());
        details.put("queryString", request.getQueryString());
        details.put("remoteAddr", getClientIpAddress(request));
        details.put("userAgent", request.getHeader("User-Agent"));
        details.put("responseStatus", response.getStatus());
        details.put("duration", duration + "ms");
        
        if (ex != null) {
            details.put("error", ex.getMessage());
        }
        
        // Add request headers (excluding sensitive ones)
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        details.put("headers", headers);
        
        try {
            return objectMapper.writeValueAsString(details);
        } catch (Exception e) {
            return "Error building audit details: " + e.getMessage();
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") || 
               lowerName.contains("password") || 
               lowerName.contains("token") || 
               lowerName.contains("secret");
    }
}
