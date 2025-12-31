package com.commercehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commercehub.dto.response.ApiResponse;
import com.commercehub.dto.response.PageResponse;
import com.commercehub.dto.response.UserResponse;
import com.commercehub.service.UserService;
import com.commercehub.util.Messages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/me")
	@Operation(summary = "Get current user", description = "Get current authenticated user profile")
	public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser()
	{
		log.debug("Request to get current user profile");
		
		UserResponse response = userService.getCurrentUser();
		
		return ResponseEntity.ok(ApiResponse.success(response));
	}
	
	@GetMapping("/{userId}")
	@PreAuthorize("#userId == authentication.principal.id or hasRole(Roles.ADMIN)")
	@Operation(summary = "Get user by ID", description = "Get user details by ID")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId)
	{
		log.debug("Request to get user by ID: {}", userId);
		 
		UserResponse response = userService.getUserById(userId);
		
		return ResponseEntity.ok(ApiResponse.success(response));
	}
	
	@GetMapping("/name/{username}")
	@Operation(summary = "Get user by username", description = "Get user details by username")
	public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username)
	{
		log.debug("Request to get user by username: {}", username);
		 
		UserResponse response = userService.getUserByUsername(username);
		
		return ResponseEntity.ok(ApiResponse.success(response));
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all users", description = "Get all users with pagination (Admin only)")
	public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
			                                                            @RequestParam(defaultValue = "20") int size,
			                                                            @RequestParam(defaultValue = "createdAt") String sortBy,
			                                                            @RequestParam(defaultValue = "DESC") String sortDir )
	{
		log.debug("Request to get all users - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
		Pageable pageable = PageRequest.of(page, size, sort);
		
		PageResponse<UserResponse> pageResponse = userService.getAllUsers(pageable);
		
		return ResponseEntity.ok(ApiResponse.success(pageResponse));
	}
	
	@GetMapping("/search")
	@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search users", description = "Search users by keyword (Admin only)")
	public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(@RequestParam String keyword,
			                                                            @RequestParam(defaultValue = "0") int page,
			                                                            @RequestParam(defaultValue = "20") int size )
	{
		log.debug("Request to search users with keyword: {}", keyword);
		
		Pageable pageable = PageRequest.of(page, size);
		
		PageResponse<UserResponse> pageResponse = userService.searchUsers(keyword, pageable);
		
		return ResponseEntity.ok(ApiResponse.success(pageResponse));
	}
	
	@PutMapping("/{userId}/profile")
	@Operation(summary = "Update user profile", description = "Update user profile information")
	public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@PathVariable Long userId, @RequestParam(required = false) String firstName,
		                                                	 @RequestParam(required = false) String lastName,
		                                                	 @RequestParam(required = false) String phoneNumber)
	{
		log.info("Request to update profile for user ID: {}", userId);
		
		UserResponse userResponse = userService.updateUserProfile(userId, firstName, lastName, phoneNumber);
		
		return ResponseEntity.ok( ApiResponse.success(Messages.USER_UPDATED_SUCCESSFULLY, userResponse) );
	}
	
	@PatchMapping("/{userId}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable/disable user", description = "Enable or disable user account (Admin only)")
	public ResponseEntity<ApiResponse<UserResponse>> setUserEnabled(@PathVariable Long userId, @RequestParam boolean enabled)
	{
		log.info("Request to set user enabled status to {} for ID: {}", enabled, userId);
		
		UserResponse userResponse = userService.setUserEnabled(userId, enabled);
		
		return ResponseEntity.ok( ApiResponse.success(Messages.USER_UPDATED_SUCCESSFULLY, userResponse) );
	}
	
    @PatchMapping("/{userId}/locked")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lock/unlock user", description = "Lock or unlock user account (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> setUserLocked(@PathVariable Long userId, @RequestParam boolean locked) 
    {
        log.info("Request to set user locked status to {} for ID: {}", locked, userId);
        
        UserResponse response = userService.setUserLocked(userId, locked);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.USER_UPDATED_SUCCESSFULLY, response));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user account (Admin only)")
    ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId)
    {
        log.info("Request to delete user with ID: {}", userId);
        
        userService.deleteUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(Messages.USER_DELETED_SUCCESSFULLY));
    }
    
    @PostMapping("/role/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add roles to user", description = "Add one or more roles to a user (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> addRoleToUser(@RequestParam @NotBlank String usernameOrEmail, @RequestParam String role) 
    {
        log.info("Request to add roles to user: {}", usernameOrEmail);

        UserResponse response = userService.addRoleToUserByUsernameOrEmail(usernameOrEmail, role);

        return ResponseEntity.ok(ApiResponse.success("Role added successfully to user", response));
    }
	
    @PostMapping("/role/remove")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove role from user", description = "Remove role from a user (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> removeRolesFromUser(@RequestParam @NotBlank String usernameOrEmail, @RequestParam String role) 
    {
        log.info("Request to remove roles from user: {}", usernameOrEmail);

        UserResponse response = userService.removeRoleFromUserByUsernameOrEmail(usernameOrEmail, role);

        return ResponseEntity.ok(ApiResponse.success("Role removed successfully from user", response));
    }
}
