package com.buildsmart.iam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - BuildSmart");
            
            String emailContent = buildPasswordResetEmailContent(resetLink);
            helper.setText(emailContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    public void sendSimpleEmail(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);
        
        mailSender.send(message);
    }
    
    private String buildPasswordResetEmailContent(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 24px; 
                        background-color: #3498db; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 4px; 
                        margin: 20px 0;
                    }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>BuildSmart</h1>
                        <p>Construction Project Planning & Site Operations Management</p>
                    </div>
                    <div class="content">
                        <h2>Password Reset Request</h2>
                        <p>Hello,</p>
                        <p>We received a request to reset your password for your BuildSmart account. Click the button below to reset your password:</p>
                        <div style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </div>
                        <p>If you didn't request this password reset, please ignore this email. The link will expire in 24 hours.</p>
                        <p>For security reasons, please make sure to:</p>
                        <ul>
                            <li>Create a strong password with at least 6 characters</li>
                            <li>Include uppercase and lowercase letters</li>
                            <li>Include at least one number</li>
                        </ul>
                        <p>Thank you for using BuildSmart!</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 BuildSmart. All rights reserved.</p>
                        <p>This is an automated message, please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetLink);
    }
}
