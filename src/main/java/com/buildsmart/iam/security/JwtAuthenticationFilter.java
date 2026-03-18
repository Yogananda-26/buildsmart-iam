package com.buildsmart.iam.security;

import com.buildsmart.iam.service.UserService;
import com.buildsmart.iam.service.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditService auditService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // Check if token is present and valid format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);

        // Reject request if token has been explicitly blacklisted (logged out)
        if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Token has been revoked. Please log in again.\"}");
            return;
        }

        userEmail = jwtService.extractEmail(jwt);
        
        // Validate token and set authentication
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = userService.findByEmail(userEmail);
            
            if (user.isPresent()) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.get().getEmail())
                        .password(user.get().getPassword())
                        .authorities("ROLE_" + user.get().getRole().name())
                        .build();
                
                if (jwtService.validateToken(jwt, user.get())) {
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Allow authentication and documentation endpoints without JWT
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/api/auth/") ||
               path.startsWith("/auth/") ||
               path.startsWith("/api/v1/swagger-ui") || 
               path.startsWith("/api/v1/v3/api-docs") ||
               path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/api/v1/actuator") ||
               path.startsWith("/actuator");
    }
}
