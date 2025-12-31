package com.commercehub.service;

import org.springframework.data.domain.Pageable;

import com.commercehub.dto.response.PageResponse;
import com.commercehub.dto.response.UserResponse;

public interface UserService {

    /**
     * Get user by ID
     * 
     * @param userId the user identifier
     * @return user response DTO
     */
    UserResponse getUserById(Long userId);

    /**
     * Get user by username
     * 
     * @param username the username
     * @return user response DTO
     */
    UserResponse getUserByUsername(String username);

    /**
     * Get all users with pagination
     * 
     * @param pageable pagination information
     * @return paginated user responses
     */
    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Search users by keyword
     * 
     * @param keyword search keyword
     * @param pageable pagination information
     * @return paginated user responses
     */
    PageResponse<UserResponse> searchUsers(String keyword, Pageable pageable);

    /**
     * Get users by role
     * 
     * @param roleName role name
     * @param pageable pagination information
     * @return paginated user responses
     */
    PageResponse<UserResponse> getUsersByRole(String roleName, Pageable pageable);

    /**
     * Update user profile
     * 
     * @param userId the user identifier
     * @param firstName new first name
     * @param lastName new last name
     * @param phoneNumber new phone number
     * @return updated user response
     */
    UserResponse updateUserProfile(Long userId, String firstName, String lastName, String phoneNumber);

    /**
     * Enable or disable user account
     * 
     * @param userId the user identifier
     * @param enabled enable status
     * @return updated user response
     */
    UserResponse setUserEnabled(Long userId, boolean enabled);

    /**
     * Lock or unlock user account
     * 
     * @param userId the user identifier
     * @param locked lock status
     * @return updated user response
     */
    UserResponse setUserLocked(Long userId, boolean locked);

    /**
     * Delete user account
     * 
     * @param userId the user identifier
     */
    void deleteUser(Long userId);

    /**
     * Get current authenticated user profile
     * 
     * @return current user response
     */
    UserResponse getCurrentUser();
    
    /**
     * Add role to user
     * 
     * @param usernameOrEmail user identifier
     * @param role to add
     * @return updated user response
     */
    UserResponse addRoleToUserByUsernameOrEmail(String usernameOrEmail, String role);
    
    /**
     * Remove roles from user
     * 
     * @param usernameOrEmail user identifier
     * @param role to remove
     * @return updated user response
     */
    UserResponse removeRoleFromUserByUsernameOrEmail(String usernameOrEmail, String role) ;

}
