package com.appointment.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    // Simple in-memory storage for token hashes
    private final Map<String, String> tokenHashStorage = new ConcurrentHashMap<>();
    private final Map<String, String> hashToTokenStorage = new ConcurrentHashMap<>();
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    // NEW: Generate and store token hash
    public String generateAndStoreToken(String username) {
        String token = generateToken(username);
        String tokenHash = hashSHA256(token);
        
        // Store both mappings for quick lookup
        tokenHashStorage.put(username, tokenHash);
        hashToTokenStorage.put(tokenHash, token);
        
        return tokenHash;
    }
    
    // NEW: Get original token from hash
    public String getTokenFromHash(String tokenHash) {
        return hashToTokenStorage.get(tokenHash);
    }
    
    // NEW: Remove token (for logout)
    public void removeToken(String tokenHash) {
        String token = hashToTokenStorage.get(tokenHash);
        if (token != null) {
            String username = extractUsername(token);
            tokenHashStorage.remove(username);
            hashToTokenStorage.remove(tokenHash);
        }
    }
    
    // NEW: Validate hash and return username
    public String validateHashAndGetUsername(String tokenHash) {
        String token = getTokenFromHash(tokenHash);
        if (token == null) {
            return null;
        }
        
        try {
            String username = extractUsername(token);
            if (!isTokenExpired(token)) {
                return username;
            } else {
                // Clean up expired token
                removeToken(tokenHash);
                return null;
            }
        } catch (Exception e) {
            // Token is invalid
            removeToken(tokenHash);
            return null;
        }
    }
    
    // NEW: Simple SHA-256 hash function
    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error creating hash", e);
        }
    }
}