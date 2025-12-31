package com.commercehub.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commercehub.dto.response.PageResponse;
import com.commercehub.dto.response.UserResponse;
import com.commercehub.entity.Role;
import com.commercehub.entity.User;
import com.commercehub.exception.BadRequestException;
import com.commercehub.exception.ForbiddenException;
import com.commercehub.exception.ResourceNotFoundException;
import com.commercehub.mapper.PageMapper;
import com.commercehub.mapper.UserMapper;
import com.commercehub.repository.RoleRepository;
import com.commercehub.repository.UserRepository;
import com.commercehub.security.SecurityUtils;
import com.commercehub.service.UserService;
import com.commercehub.util.RoleName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService
 * Handles user management business logic
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    
    private final RoleRepository roleRepository;
	
	private final UserMapper userMapper;
	
	private final PageMapper pageMapper;

	
    /**
     * Check if current user can access the specified user's profile
     * User can access their own profile, or admin can access any profile
     */
    private void checkUserAccess(Long userId) 
    {
        if (!SecurityUtils.isOwnerOrHasAuthority(userId, RoleName.ROLE_ADMIN.name())) {
            throw new ForbiddenException("You don't have permission to access this user profile");
        }
    }
	
    @Transactional(readOnly = true)
	@Override
	public UserResponse getUserById(Long userId)
	{
        log.debug("Fetching user by ID: {}", userId);
        
		// Check if user can access this profile
        checkUserAccess(userId);
        
		User user = userRepository.findById(userId).orElseThrow( () -> new ResourceNotFoundException("User", "id", userId));
		
		return userMapper.toResponse(user);
	}
	
    @Transactional(readOnly = true)
    @Override
	public UserResponse getUserByUsername(String username)
	{
        log.debug("Fetching user by ID: {}", username);
                
		User user = userRepository.findByUsername(username).orElseThrow( () -> new ResourceNotFoundException("User", "id", username));
		
		// Check if user can access this profile
        checkUserAccess(user.getId());
        
		return userMapper.toResponse(user);
	}
	
    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable)
    {
    	log.debug("Fetching all users with pagination");
    	
    	// Only admins can search users
    	if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
    	{
    		throw new ForbiddenException("You don't have permission to view all users");
    	}
    	
        Page<User> userPage = userRepository.findAll(pageable);
        
        return pageMapper.toPageResponse(userPage, userMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    @Override
	public PageResponse<UserResponse> searchUsers(String keyword, Pageable pageable)
	{
		log.debug("Searching users with keyword: {}", keyword);
		
		// Only admins can search users
    	if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
    	{
    		throw new ForbiddenException("You don't have permission to view all users");
    	}
    	
		Page<User> userPage = userRepository.searchUsers(keyword, pageable);
		
		return pageMapper.toPageResponse(userPage, userMapper::toResponse);
	}
    
    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserResponse> getUsersByRole(String roleName, Pageable pageable)
    {
    	log.debug("Fetching users by role: {}", roleName);
    	
    	if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
    	{
    		throw new ForbiddenException("You don't have permission to view all users");
    	}
    	
		Page<User> userPage = userRepository.findByRoleName(roleName, pageable);
		
		return pageMapper.toPageResponse(userPage, userMapper::toResponse);
    }

    @Transactional
	@Override
	public UserResponse updateUserProfile(Long userId, String firstName, String lastName, String phoneNumber) 
    {
    	log.info("Updating user profile for user ID: {}", userId);
        
        // Check if user can update this profile
        checkUserAccess(userId);
        
        User user = userRepository.findById(userId).orElseThrow( () -> new ResourceNotFoundException("User", "id", userId));
        
        if(firstName != null)
        	user.setFirstName(firstName);
        if(lastName != null)
        	user.setLastName(lastName);
        if(phoneNumber != null)
        	user.setPhoneNumber(phoneNumber);
        
        User updateUser = userRepository.save(user);
        
        log.info("User profile updated successfully for ID: {}", userId);
        
		return userMapper.toResponse(updateUser);
	}

    @Transactional
	@Override
	public UserResponse setUserEnabled(Long userId, boolean enabled) 
    {
    	 log.info("Setting user enabled status to {} for user ID: {}", enabled, userId);
    	 
     	if(!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name()))
     	{
     		throw new ForbiddenException("You don't have permission to view all users");
     	}
     	
     	User user = userRepository.findById(userId).orElseThrow( () -> new ResourceNotFoundException("User", "id", userId));
     	
     	user.setIsEnable(enabled);
     	
        User updateUser = userRepository.save(user);
        
        log.info("User enabled status updated successfully for ID: {}", userId);
        
		return userMapper.toResponse(updateUser);
	}

    @Transactional
	@Override
	public UserResponse setUserLocked(Long userId, boolean locked) 
	{
        log.info("Setting user locked status to {} for user ID: {}", locked, userId);
        
        // Only admins can lock/unlock users
        if (!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name())) {
            throw new ForbiddenException("You don't have permission to lock/unlock users");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setIsLocked(locked);
        
        User updatedUser = userRepository.save(user);
        
        log.info("User locked status updated successfully for ID: {}", userId);
        
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
	@Override
	public void deleteUser(Long userId) 
	{
        log.info("Deleting user with ID: {}", userId);
        
        // Only admins can delete users
        if (!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name())) 
        {
            throw new ForbiddenException("You don't have permission to delete users");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        userRepository.delete(user);
        
        log.info("User deleted successfully with ID: {}", userId);		
	}

    @Transactional(readOnly = true)
	@Override
	public UserResponse getCurrentUser() 
	{
        log.debug("Fetching current authenticated user");
        
        Long userId = SecurityUtils.getCurrentUserIdOrThrow();
        
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return userMapper.toResponse(user);
	}

    @Override
    @Transactional
    public UserResponse addRoleToUserByUsernameOrEmail(String usernameOrEmail, String role) 
    {
        log.info("Adding roles to user with usernameOrEmail: {}", usernameOrEmail);
        
        // Check permission Only admins can add role
        if (!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name())) 
        {
            throw new ForbiddenException("You don't have permission to add role");
        }

        // Get user
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow( () -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        // Parse and validate roles
        String normalizedRoleName = role.trim().toUpperCase();
        
        if(!normalizedRoleName.startsWith("ROLE_"))
        {
        	normalizedRoleName = "ROLE_" + normalizedRoleName;
        }
     
        // Make final variable for lambda usage
        final String roleName = normalizedRoleName;
        
        //Check if user already has this role
        if( user.getRoles().stream().anyMatch( r -> r.getName().equals(roleName) ) )
        {
        	throw new BadRequestException("User already has role: " + roleName);
        }

        // Get role entity
        Role userRole = roleRepository.findByName(roleName).orElseThrow( () -> new ResourceNotFoundException("Role", "name", roleName) );

        // Add roles to user
        user.addRole(userRole);

        // Save user
        User updatedUser = userRepository.save(user);
        log.info("Successfully added role: {} to user: {}", roleName, usernameOrEmail);

        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    @Transactional
    public UserResponse removeRoleFromUserByUsernameOrEmail(String usernameOrEmail, String role) 
    {
        log.info("Removing roles from user with usernameOrEmail: {}", usernameOrEmail);
        
        // Check permission Only admins can delete role
        if (!SecurityUtils.hasAuthority(RoleName.ROLE_ADMIN.name())) 
        {
            throw new ForbiddenException("You don't have permission to delete role");
        }

        // Get user
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow( () -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        Long myId = SecurityUtils.getCurrentUserIdOrThrow();
        
        if(myId == user.getId())
        {
        	throw new ForbiddenException("You don't have permission to delete yourself");
        }
        
        // Parse and validate roles
        String normalizedRoleName = role.trim().toUpperCase();
        
        if(!normalizedRoleName.startsWith("ROLE_"))
        {
        	normalizedRoleName = "ROLE_" + normalizedRoleName;
        }
     
        // Make final variable for lambda usage
        final String roleName = normalizedRoleName;
        
        //Check if user already has this role
        if( !user.getRoles().stream().anyMatch( r -> r.getName().equals(roleName) ) )
        {
        	throw new BadRequestException("User already not has role: " + roleName);
        }
        
        // Get role entity
        Role userRole = roleRepository.findByName(roleName).orElseThrow( () -> new ResourceNotFoundException("Role", "name", roleName) );

        // Remove roles from user
        user.removeRole(userRole);

        // Save user
        User updatedUser = userRepository.save(user);
        
        log.info("Successfully removed role:{}  from user : {}", roleName, usernameOrEmail);

        return userMapper.toResponse(updatedUser);
    }

}
