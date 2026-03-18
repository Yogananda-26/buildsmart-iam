package com.buildsmart.iam.service;

import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to generate custom User IDs based on role-specific prefixes.
 * ID Format: {ROLE_PREFIX}{SEQUENCE_NUMBER}
 * Example: BSAD001, BSPM002, BSVM003
 */
@Service
@Transactional
public class UserIdGeneratorService {
    
    @Autowired
    private UserRepository userRepository;
    
    private static final String PREFIX_ADMIN = "BSAD";
    private static final String PREFIX_PROJECT_MANAGER = "BSPM";
    private static final String PREFIX_SITE_ENGINEER = "BSSE";
    private static final String PREFIX_SAFETY_OFFICER = "BSSO";
    private static final String PREFIX_VENDOR = "BSVM";
    private static final String PREFIX_FINANCE_OFFICER = "BSFO";
    private static final int SEQUENCE_LENGTH = 3;
    
    /**
     * Generate a custom user ID based on the user's role.
     * 
     * @param role The role of the user
     * @return Generated user ID with format: {PREFIX}{SEQUENCE}
     */
    public String generateUserId(Role role) {
        String prefix = getRolePrefix(role);
        long nextSequence = getNextSequenceForRole(role);
        return String.format("%s%0" + SEQUENCE_LENGTH + "d", prefix, nextSequence);
    }
    
    /**
     * Get the role-specific prefix for the user ID.
     * 
     * @param role The user's role
     * @return The prefix for the role
     */
    private String getRolePrefix(Role role) {
        return switch (role) {
            case ADMIN -> PREFIX_ADMIN;
            case PROJECT_MANAGER -> PREFIX_PROJECT_MANAGER;
            case SITE_ENGINEER -> PREFIX_SITE_ENGINEER;
            case SAFETY_OFFICER -> PREFIX_SAFETY_OFFICER;
            case VENDOR -> PREFIX_VENDOR;
            case FINANCE_OFFICER -> PREFIX_FINANCE_OFFICER;
        };
    }
    
    /**
     * Get the next sequence number for a specific role prefix.
     * Counts existing users with the same role prefix and returns count + 1.
     * 
     * @param role The user's role
     * @return The next sequence number
     */
    private long getNextSequenceForRole(Role role) {
        long currentCount = userRepository.countByRole(role);
        return currentCount + 1;
    }
}
