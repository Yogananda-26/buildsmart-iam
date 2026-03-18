package com.buildsmart.iam.service;

import com.buildsmart.iam.entity.AuditLog;
import com.buildsmart.iam.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public void logAction(String userId, String action, String resource, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setDetails(details);
        
        auditLogRepository.save(auditLog);
    }
    
    public void logAction(String userId, String action, String resource, String details, 
                         HttpServletRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setDetails(details);
        auditLog.setIpAddress(getClientIpAddress(request));
        auditLog.setUserAgent(request.getHeader("User-Agent"));
        
        auditLogRepository.save(auditLog);
    }
    
    public List<AuditLog> getAuditLogsByUserId(String userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }
    
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
    
    public List<AuditLog> getAuditLogsByUserAndAction(String userId, String action) {
        return auditLogRepository.findByUserIdAndAction(userId, action);
    }
    
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    public long getActionCountSince(String action, LocalDateTime since) {
        return auditLogRepository.countByActionSince(action, since);
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
}
