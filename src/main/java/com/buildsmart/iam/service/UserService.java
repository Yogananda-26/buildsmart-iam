package com.buildsmart.iam.service;

import com.buildsmart.iam.dto.AuthResponse;
import com.buildsmart.iam.dto.LoginRequest;
import com.buildsmart.iam.dto.SignupRequest;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.entity.UserStatus;
import com.buildsmart.iam.repository.UserRepository;
import com.buildsmart.iam.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private UserIdGeneratorService userIdGeneratorService;
    
    public User registerUser(SignupRequest signupRequest, HttpServletRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        
        // Check if phone already exists
        if (userRepository.existsByPhone(signupRequest.getPhone())) {
            throw new RuntimeException("Phone number is already registered");
        }
        
        // Create new user
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        // Set role (default to VENDOR if not specified)
        Role role = signupRequest.getRole() != null ? signupRequest.getRole() : Role.VENDOR;
        user.setRole(role);
        
        // Generate custom user ID based on role
        String customUserId = userIdGeneratorService.generateUserId(role);
        user.setUserId(customUserId);
        
        // Set status to PENDING_VERIFICATION for new users (except admin)
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Log audit with IP and User-Agent
        if (request != null) {
            auditService.logAction(savedUser.getUserId(), "USER_REGISTRATION", 
                    "User", "New user registered with email: " + savedUser.getEmail(), request);
        }
        
        return savedUser;
    }
    
    public AuthResponse authenticateUser(LoginRequest loginRequest, HttpServletRequest request) {
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            if (request != null) {
                auditService.logAction(user.getUserId(), "LOGIN_FAILED", 
                        "Authentication", "Failed login attempt for email: " + user.getEmail(), request);
            }
            throw new RuntimeException("Invalid email or password");
        }
        
        // Check user status
        if (user.getStatus() != UserStatus.ACTIVE) {
            String statusMessage = switch (user.getStatus()) {
                case PENDING_VERIFICATION -> "Account is pending verification. Please wait for admin approval.";
                case INACTIVE -> "Account is inactive. Please contact administrator.";
                case SUSPENDED -> "Account is suspended. Please contact administrator.";
                default -> "Account is not active. Please contact administrator.";
            };
            throw new RuntimeException(statusMessage);
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Log successful login with IP and User-Agent
        if (request != null) {
            auditService.logAction(user.getUserId(), "LOGIN_SUCCESS", 
                    "Authentication", "User logged in successfully", request);
        }
        
        // Create auth response
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setType("Bearer");
        authResponse.setExpiresIn(86400L); // 24 hours
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());
        authResponse.setPhone(user.getPhone());
        authResponse.setRole(user.getRole());
        
        return authResponse;
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    
    public User updateUser(String userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(userDetails.getName());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        user.setStatus(userDetails.getStatus());
        
        User updatedUser = userRepository.save(user);
        
        auditService.logAction(userId, "USER_UPDATED", 
                "User", "User details updated");
        
        return updatedUser;
    }
    
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userRepository.delete(user);
        
        auditService.logAction(userId, "USER_DELETED", 
                "User", "User deleted: " + user.getEmail());
    }
    
    // Overloaded methods for backward compatibility with Long userId
    public User updateUser(Long userId, User userDetails) {
        return updateUser(String.valueOf(userId), userDetails);
    }
    
    public void deleteUser(Long userId) {
        deleteUser(String.valueOf(userId));
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}
