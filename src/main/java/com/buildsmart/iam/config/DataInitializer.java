package com.buildsmart.iam.config;

import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.UserStatus;
import com.buildsmart.iam.repository.UserRepository;
import com.buildsmart.iam.service.UserIdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserIdGeneratorService userIdGeneratorService;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        // Check if admin user already exists
        if (userRepository.existsByEmail("admin@buildsmart.com")) {
            logger.info("Default admin user already exists");
            return;
        }

        // Create default admin user
        User admin = new User();
        
        // Generate custom user ID for admin role
        String adminId = userIdGeneratorService.generateUserId(Role.ADMIN);
        admin.setUserId(adminId);
        
        admin.setName("System Administrator");
        admin.setEmail("admin@buildsmart.com");
        admin.setPhone("0000000000");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        userRepository.save(admin);
        logger.info("Default admin user created successfully");
        logger.info("User ID: " + adminId);
        logger.info("Email: admin@buildsmart.com");
        logger.info("Password: admin123");
    }
}
