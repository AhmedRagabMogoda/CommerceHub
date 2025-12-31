package com.commercehub.security;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.commercehub.exception.UnauthorizedException;

/**
 * Security utility methods
 * Provides helper methods for accessing security context
 */

public class SecurityUtils {
	
	private SecurityUtils()
	{
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
	
    /**
     * Get current authenticated user principal from SecurityContext
     */
    public static Optional<UserPrincipal> getCurrentUserPrincipal()
    {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if(authentication != null && authentication.getPrincipal() instanceof UserPrincipal)
    	{
    		return Optional.of( (UserPrincipal) authentication.getPrincipal() );
    	}
    	return Optional.empty();
    }
    
    /**
     * Get current authenticated user principal or throw exception
     */
    public static UserPrincipal getCurrentUserPrincipalOrThrow() 
    {
        return getCurrentUserPrincipal().orElseThrow( () -> new UnauthorizedException("User is not authenticated"));
    }
    
    /**
     * Get current authenticated user ID
     */
    public static Optional<Long> getCurrentUserId() 
    {
        return getCurrentUserPrincipal().map(UserPrincipal::getId);
    }
    
    /**
     * Get current authenticated user ID or throw exception
     */
    public static Long getCurrentUserIdOrThrow() 
    {
        return getCurrentUserId().orElseThrow( () -> new UnauthorizedException("User is not authenticated"));
    }
    
    /**
     * Check if current user is the owner of resource
     */
    public static boolean isOwner(Long resourceOwnerId) 
    {
        return getCurrentUserId().map( userId -> userId.equals(resourceOwnerId)).orElse(false);
    }
    
    /**
     * Get current authenticated username
     */
    public static Optional<String> getCurrentUsername() 
    {
        return getCurrentUserPrincipal().map(UserPrincipal::getUsername);
    }

    /**
     * Get current authenticated username or throw exception
     */
    public static String getCurrentUsernameOrThrow() 
    {
        return getCurrentUsername().orElseThrow( () -> new UnauthorizedException("User is not authenticated"));
    }
    
    /**
     * Get current user's authorities (roles)
     */
    public static Set<String> getCurrentUserAuthorities()
    {
    	return getCurrentUserPrincipal().map( principal -> principal.getAuthorities().stream()
    																				 .map(GrantedAuthority::getAuthority)
    																				 .collect(Collectors.toSet())
    			                            ).orElse(Set.of());
    }
    
    /**
     * Check if current user has specific authority
     */
    public static boolean hasAuthority(String authority) 
    {
        return getCurrentUserAuthorities().contains(authority);
    }

    /**
     * Check if current user has any of the specified authorities
     */
    public static boolean hasAnyAuthority(String... authorities) 
    {
        Set<String> userAuthorities = getCurrentUserAuthorities();
        
        for (String authority : authorities) {
            if (userAuthorities.contains(authority)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if current user has all of the specified authorities
     */
    public static boolean hasAllAuthorities(String... authorities) 
    {
        Set<String> userAuthorities = getCurrentUserAuthorities();
        
        for (String authority : authorities) {
            if (!userAuthorities.contains(authority)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * Check if current user is owner or has specific authority
     */
    public static boolean isOwnerOrHasAuthority(Long resourceOwnerId, String authority) 
    {
        return isOwner(resourceOwnerId) || hasAuthority(authority);
    }

    /**
     * Clear security context
     */
    public static void clearSecurityContext() 
    {
        SecurityContextHolder.clearContext();
    }
    
}
