package com.commercehub.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.commercehub.util.JwtConstants;


/**
 * JWT Authentication Filter
 * Intercepts requests and validates JWT tokens
 * Sets authentication in SecurityContext if valid
 */

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

	    try {
	        // Extract JWT from request header
	        String jwt = extractJwtFromRequest(request);

	        // Proceed only if JWT is present and valid
	        if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {

	            String username = jwtService.extractUsername(jwt);

	            // Only set authentication if not already authenticated
	            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

	                // Ensure token is not expired
	                if (jwtService.isTokenValid(jwt)) {

	                    // Extract UserPrincipal directly from token
	                    UserPrincipal userPrincipal = jwtService.extractUserPrincipal(jwt);

	                    // Build authentication token
	                    UsernamePasswordAuthenticationToken authentication =
	                            new UsernamePasswordAuthenticationToken(
	                                    userPrincipal,              /** Principal must be UserPrincipal **/
	                                    null,                       /** No credentials needed */
	                                    userPrincipal.getAuthorities() /** Roles/Authorities **/
	                            );

	                    // Add request details
	                    authentication.setDetails(
	                            new WebAuthenticationDetailsSource().buildDetails(request)
	                    );

	                    // Set authentication in security context
	                    SecurityContextHolder.getContext().setAuthentication(authentication);

	                    log.debug("Set authentication for user '{}' without database call", username);
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("Cannot set user authentication: {}", e.getMessage());
	        SecurityContextHolder.clearContext();
	    }

	    // Continue filter chain
	    filterChain.doFilter(request, response);
	}


    /**
     * Extract JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstants.HEADER_STRING);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length());
        }
        
        return null;
    }
}
