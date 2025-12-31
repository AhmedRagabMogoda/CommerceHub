package com.commercehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commercehub.dto.request.LoginUserRequest;
import com.commercehub.dto.request.RegisterUserRequest;
import com.commercehub.dto.response.ApiResponse;
import com.commercehub.dto.response.AuthResponse;
import com.commercehub.service.AuthService;
import com.commercehub.util.Messages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	
	
	@PostMapping("/register")
	@Operation(summary = "Register new user", description = "Create a new user account")
	ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterUserRequest request)
	{
		log.info("Registration request received for username: {}", request.getUsername());
		
		AuthResponse response = authService.register(request);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				             .body(ApiResponse.success(Messages.USER_REGISTERED_SUCCESSFULLY, response));
	}
	
	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and generate tokens")
	ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginUserRequest request)
	{
		log.info("Login request received for: {}", request.getUsernameOrEmail());
		
		AuthResponse response = authService.login(request);
		
		return ResponseEntity.ok( ApiResponse.success(Messages.LOGIN_SUCCESSFUL, response) );
	}
	
	@PostMapping("/refresh")
	@Operation(summary = "Refresh token", description = "Generate new access token using refresh token")
	ResponseEntity<ApiResponse<AuthResponse>> register(@RequestParam String refreshToken)
	{
		log.info("Token refresh request received");
		
		AuthResponse response = authService.refreshToken(refreshToken);
		
		return ResponseEntity.ok( ApiResponse.success(Messages.REFRESH_TOKEN, response) );
	}
	
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) 
    {
        log.info("Logout request received");
        
        authService.logout(token);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.LOGOUT_SUCCESSFUL));
    }
	
}
