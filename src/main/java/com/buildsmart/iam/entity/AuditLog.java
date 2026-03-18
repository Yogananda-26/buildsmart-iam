package com.buildsmart.iam.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;
    
    @Column(name = "user_id", length = 20)
    private String userId;
    
    @Column(name = "action", nullable = false, length = 100)
    private String action;
    
    @Column(name = "resource", length = 255)
    private String resource;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @CreatedDate
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Default constructor
    public AuditLog() {}
    
    // Constructor with required fields
    public AuditLog(String userId, String action) {
        this.userId = userId;
        this.action = action;
    }
    
    // Constructor with all fields
    public AuditLog(String userId, String action, String resource, String details, String ipAddress, String userAgent) {
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.details = details;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    // Getters and Setters
    public Long getAuditId() {
        return auditId;
    }
    
    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
