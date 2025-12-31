package com.commercehub.service;

import com.commercehub.dto.request.LoginUserRequest;
import com.commercehub.dto.request.RegisterUserRequest;
import com.commercehub.dto.response.AuthResponse;

/**
 * Service interface for authentication operations
 * Handles user registration, login, and token management
 */

public interface AuthService {

    /**
     * Register a new user account
     * 
     * @param request registration details
     * @return authentication response with tokens
     */
    AuthResponse register(RegisterUserRequest request);

    /**
     * Authenticate user and generate tokens
     * 
     * @param request login credentials
     * @return authentication response with tokens
     */
    AuthResponse login(LoginUserRequest request);

    /**
     * Refresh access token using refresh token
     * 
     * @param refreshToken the refresh token
     * @return new authentication response with tokens
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * Logout user and invalidate tokens
     * 
     * @param token the access token to invalidate
     */
    void logout(String token);
}