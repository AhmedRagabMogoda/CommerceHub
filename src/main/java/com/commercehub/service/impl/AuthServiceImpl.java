package com.commercehub.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commercehub.dto.request.LoginUserRequest;
import com.commercehub.dto.request.RegisterUserRequest;
import com.commercehub.dto.response.AuthResponse;
import com.commercehub.entity.Role;
import com.commercehub.entity.User;
import com.commercehub.exception.DuplicateResourceException;
import com.commercehub.exception.ResourceNotFoundException;
import com.commercehub.exception.UnauthorizedException;
import com.commercehub.mapper.UserMapper;
import com.commercehub.repository.RoleRepository;
import com.commercehub.repository.UserRepository;
import com.commercehub.security.JwtService;
import com.commercehub.security.UserPrincipal;
import com.commercehub.service.AuthService;
import com.commercehub.util.JwtConstants;
import com.commercehub.util.RoleName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService
 * Handles authentication and user registration business logic
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
	
	private final UserRepository userRepository;
	
	private final UserMapper userMapper;
	
	private final PasswordEncoder passwordEncoder;
	
	private final RoleRepository roleRepository;
	
	private final JwtService jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	@Transactional
	@Override
	public AuthResponse register(RegisterUserRequest request)
	{
		log.info("Registering new user with username: {}",request.getUsername());
		
		// Check if username already exists
		if(userRepository.existsByUsername(request.getUsername()))
		{
			 throw new DuplicateResourceException("User", "username", request.getUsername());
		}
		
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) 
        {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
		
		// Create new user entity
		User user = userMapper.toEntity(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		// Assign default role (USER)
		Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name()).orElseThrow( () -> new ResourceNotFoundException("Role", "name",RoleName.ROLE_USER.name()));
		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);
		
		//Save user
		User savedUser = userRepository.save(user);
		log.info("User registered successfully with ID: {}", savedUser.getId());
		
		UserPrincipal userPrincipal = UserPrincipal.create(savedUser);
		
		// Generate tokens
		String accessToken = jwtService.generateToken(userPrincipal, savedUser.getId());
		String refreshToken = jwtService.generateRefreshToken(userPrincipal, savedUser.getId());
		
		// Build and return response
		return AuthResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.tokenType(JwtConstants.TOKEN_TYPE)
				.userId(savedUser.getId())
				.username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .roles(savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
	}
	
	@Transactional
	@Override
	public AuthResponse login(LoginUserRequest request)
	{
		log.info("User attempting to login: {}",request.getUsernameOrEmail());
		
		try {
			
		// Authenticate user
		Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken( request.getUsernameOrEmail(), request.getPassword() ) );
		
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		
		// Update last login time
		userRepository.updateLastLoginAt(userPrincipal.getId(), LocalDateTime.now());
		
		// Generate tokens
		String accessToken = jwtService.generateToken(userPrincipal, userPrincipal.getId());
		String refreshToken = jwtService.generateRefreshToken(userPrincipal, userPrincipal.getId());
		
	     // Build and return response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(JwtConstants.TOKEN_TYPE)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .firstName(userPrincipal.getFirstName())
                .lastName(userPrincipal.getLastName())
                .roles( userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()) )
                .build();
        
		} catch(BadCredentialsException e)
		{
			log.error("Login failed for user: {}", request.getUsernameOrEmail());
            throw new UnauthorizedException("Invalid username or password");
		}
		
	}
	
	@Transactional
	@Override
	public AuthResponse refreshToken(String refreshToken)
	{
		log.info("Refreshing access token");

        // Validate refresh token
		if(!jwtService.validateToken(refreshToken))
		{
			throw new UnauthorizedException("Invalid or expired refresh token");
		}
		
		// Extract username from token
		String username = jwtService.extractUsername(refreshToken);
		Long userId = jwtService.extractUserId(refreshToken);
		
		// Load user details
		User user = userRepository.findByUsername(username).orElseThrow( () -> new ResourceNotFoundException("User", "username", username));
		
		UserPrincipal userPrincipal = UserPrincipal.create(user);
		
		// Generate tokens
		String newAccessToken = jwtService.generateToken(userPrincipal, userId);
		String newRefreshToken = jwtService.generateRefreshToken(userPrincipal, user.getId());
		
		log.info("Token refreshed successfully for user: {}", username);
		
		// Build and return response
		return AuthResponse.builder()
	                .accessToken(newAccessToken)
	                .refreshToken(newRefreshToken)
	                .tokenType(JwtConstants.TOKEN_TYPE)
	                .userId(user.getId())
	                .username(user.getUsername())
	                .email(user.getEmail())
	                .firstName(user.getFirstName())
	                .lastName(user.getLastName())
	                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
	                .build();
	}
	
    @Override
    public void logout(String token) 
    {
        log.info("User logging out");
        // In a stateless JWT system, logout is typically handled client-side
        // by removing the token from storage
        // If you need server-side token invalidation, you can implement
        // a token blacklist using Redis
    }

}
