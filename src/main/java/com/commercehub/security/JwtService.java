package com.commercehub.security;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.commercehub.util.JwtConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for JWT token operations
 * Handles token generation, validation, and extraction of claims
 */

@Service
@Slf4j
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;
	
	@Value("${jwt.expiration}")
	private long jwtExpiration;
	
	@Value("${jwt.refresh-expiration}")
	private long refreshExpiration;
	
	
	 /**
     * Extract a specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
	
    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(JwtConstants.USER_ID_KEY, Long.class));
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract authorities from JWT token
     */
    public List<String> extractAuthorities(String token) 
    {
        return extractClaim(token, claims -> {
            List<?> authorities = claims.get(JwtConstants.AUTHORITIES_KEY, List.class);
            if (authorities == null) 
            {
                return Collections.emptyList();
            }
            return authorities.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        });
    }

    /**
     * Extract user principal from JWT token without database call
     */
    public UserPrincipal extractUserPrincipal(String token) 
    {
        Claims claims = extractAllClaims(token);
        
        // Extract username from claims
        String username = claims.getSubject();
        
        // Extract userId from claims
        Long userId = claims.get(JwtConstants.USER_ID_KEY, Long.class);

        // Extract authorities from claims
        List<?> authoritiesList = claims.get(JwtConstants.AUTHORITIES_KEY, List.class);
        
        List<SimpleGrantedAuthority> authorities = (authoritiesList != null) ? authoritiesList.stream()
															                        .map(Object::toString)
															                        .map(SimpleGrantedAuthority::new)
															                        .collect(Collectors.toList())
															                : Collections.emptyList();

        // Create UserPrincipal instead of Spring User
        return UserPrincipal.builder()
                .username(username)
                .id(userId)
                .authorities(authorities)
                .build();
    }
    
    /**
     * Generate JWT access token for user with all necessary information
     */
    public String generateToken(UserDetails userDetails, Long userId) 
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID_KEY, userId);
        claims.put(JwtConstants.AUTHORITIES_KEY, 
                   userDetails.getAuthorities().stream()
                           .map(GrantedAuthority::getAuthority)
                           .collect(Collectors.toList()));
        
        return generateToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    /**
     * Generate JWT refresh token for user
     */
    public String generateRefreshToken(UserDetails userDetails, Long userId) 
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID_KEY, userId);
        
        return generateToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    /**
     * Generate JWT token with claims and expiration
     */
    private String generateToken(Map<String, Object> extraClaims, String subject, long expiration) 
    {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validate JWT token without database call
     * Only checks token structure, signature, and expiration
     */
    public boolean isTokenValid(String token) 
    {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token) && claims.getSubject() != null;
        } catch (Exception e) {
            log.error("Error validating JWT token", e);
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Get signing key from secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validate token structure and signature
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        } catch (Exception e) {
            log.error("JWT token validation error", e);
        }
        return false;
    }

    /**
     * Get token expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Get refresh token expiration time in milliseconds
     */
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }
}