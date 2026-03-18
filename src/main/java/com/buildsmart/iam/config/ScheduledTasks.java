package com.buildsmart.iam.config;

import com.buildsmart.iam.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    // Clean up expired password reset tokens every hour
    @Scheduled(fixedRate = 3600000)
    // 1 hour in milliseconds
    public void cleanupExpiredTokens() {
        try {
            passwordResetService.cleanupExpiredTokens();
            System.out.println("Expired password reset tokens cleaned up successfully");
        } catch (Exception e) {
            System.err.println("Error cleaning up expired tokens: " + e.getMessage());
        }
    }
}
