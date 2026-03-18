package com.buildsmart.iam.security;

import com.buildsmart.iam.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    // Get signing key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    // Extract username from token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Extract specific claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // Generate token for user
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        return createToken(claims, user.getEmail());
    }
    
    // Create token with claims
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Validate token
    public Boolean validateToken(String token, User user) {
        try {
            final String email = extractEmail(token);
            return (email.equals(user.getEmail()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // Validate token format
    public Boolean validateTokenFormat(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // Get user ID from token
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }
    
    // Get user role from token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    // Get user name from token
    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }
}
