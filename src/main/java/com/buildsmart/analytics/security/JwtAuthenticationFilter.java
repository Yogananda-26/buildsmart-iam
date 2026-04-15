package com.buildsmart.analytics.security;

import com.buildsmart.analytics.client.IamServiceClient;
import com.buildsmart.analytics.client.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Filter that intercepts every HTTP request, reads the Authorization header,
 * validates the JWT token, and sets the Spring Security context.
 *
 * Flow:
 * 1. Read "Authorization: Bearer <token>" header
 * 2. If present, validate the token using JwtUtil
 * 3. Extract userId and call IAM module to check user status
 * 4. Only ACTIVE users are allowed; INACTIVE/SUSPENDED users get 403
 * 5. If valid and active, set SecurityContext with username + roles
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final IamServiceClient iamServiceClient;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, IamServiceClient iamServiceClient) {
        this.jwtUtil = jwtUtil;
        this.iamServiceClient = iamServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Only process if the header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                String userId = jwtUtil.extractUserId(token);
                List<String> roles = jwtUtil.extractRoles(token);

                log.info("JWT valid for user: '{}' (userId: {}), roles: {}", username, userId, roles);

                // Check user status from IAM module
                if (userId != null) {
                    try {
                        UserDTO user = iamServiceClient.getUserById(userId);
                        String status = user.status();

                        log.info("User '{}' status from IAM: {}", userId, status);

                        if (!"ACTIVE".equalsIgnoreCase(status)) {
                            log.warn("Access denied — user '{}' status is: {}", userId, status);
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(
                                    "{\"error\": \"Access denied\", \"message\": \"User account is " + status + ". Please contact your administrator.\"}" );
                            return; // Do NOT continue the filter chain
                        }
                    } catch (Exception e) {
                        log.warn("Could not verify user status from IAM for userId '{}': {}", userId, e.getMessage());
                        // Fail-open: allow access if IAM is unreachable (fallback handles this)
                    }
                }

                // Convert roles to Spring Security authorities (prefixed with ROLE_)
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .toList();

                log.info("Granted authorities: {}", authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("JWT token validation failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}
