package com.commercehub.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.commercehub.dto.request.RegisterUserRequest;
import com.commercehub.dto.response.UserResponse;
import com.commercehub.entity.Role;
import com.commercehub.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
    /**
     * Convert User entity to UserResponse DTO
     * Maps roles collection to role names set 
     */
	@Mapping(target = "roles", source = "roles",qualifiedByName = "rolesToRoleNames")
	UserResponse toResponse(User user);
	
	@Named("rolesToRoleNames")
	default Set<String> mapRolesToRoleNames(Set<Role> roles)
	{
		if(roles == null)
			return Set.of();
		
		return roles.stream()
				    .map(Role::getName)
				    .collect(Collectors.toSet());
	}
	
    /**
     * Convert RegisterRequest DTO to User entity
     * Password should be encoded before calling this method
     */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "orders", ignore = true)
	@Mapping(target = "lastLoginAt", ignore = true)
	@Mapping(target = "isEnable", constant = "true")
	@Mapping(target = "isLocked", constant = "false")
	@Mapping(target = "emailVerified", constant = "false")
	User toEntity(RegisterUserRequest request);

    /**
     * Update existing User entity from RegisterRequest
     * Ignores password and other sensitive fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "isEnable", ignore = true)
    @Mapping(target = "isLocked", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "statusCode", ignore = true)
    void updateEntityFromRequest(RegisterUserRequest request, @MappingTarget User user);
}
