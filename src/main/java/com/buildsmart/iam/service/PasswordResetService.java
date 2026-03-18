package com.buildsmart.iam.service;

import com.buildsmart.iam.dto.ForgotPasswordRequest;
import com.buildsmart.iam.dto.ResetPasswordRequest;
import com.buildsmart.iam.entity.PasswordResetToken;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.repository.PasswordResetTokenRepository;
import com.buildsmart.iam.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuditService auditService;
    
    public void forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            // Don't reveal that user doesn't exist for security
            return;
        }
        
        User user = userOpt.get();
        
        // Check if user is active
        if (user.getStatus() != com.buildsmart.iam.entity.UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // Token expires in 24 hours
        
        // Invalidate existing tokens for this user
        tokenRepository.markTokensAsUsedByUser(user);
        
        // Create new token
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, resetToken, expiresAt);
        tokenRepository.save(passwordResetToken);
        
        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        
        // Log audit
        auditService.logAction(user.getUserId(), "PASSWORD_RESET_REQUESTED",
                "Password Reset", "Password reset requested for email: " + user.getEmail(), httpRequest);
    }
    
    public void resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Find token
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(request.getToken());
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired reset token");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        // Validate token
        if (!resetToken.isValid()) {
            throw new RuntimeException("Invalid or expired reset token");
        }
        
        User user = resetToken.getUser();
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        // Log audit
        auditService.logAction(user.getUserId(), "PASSWORD_RESET_COMPLETED",
                "Password Reset", "Password reset completed successfully", httpRequest);
        
        // Send confirmation email (optional)
        emailService.sendSimpleEmail(user.getEmail(), 
                "Password Reset Successful - BuildSmart", 
                "Your password has been successfully reset. If you didn't make this change, please contact support immediately.");
    }
    
    public void cleanupExpiredTokens() {
        List<PasswordResetToken> expiredTokens = 
                tokenRepository.findByUsedFalseAndExpiresAtBefore(LocalDateTime.now());
        
        for (PasswordResetToken token : expiredTokens) {
            token.setUsed(true);
            tokenRepository.save(token);
        }
    }
    
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.isPresent() && tokenOpt.get().isValid();
    }
}
