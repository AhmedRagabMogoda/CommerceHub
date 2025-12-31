package com.commercehub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.commercehub.entity.User;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	
    /**
     * Find a user by username
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
	Optional<User> findByUsername(String userName);
	
	/**
     * Find a user by email address
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
	Optional<User> findByEmail(String email);
	
    /**
     * Count users by enabled status
     * 
     * @param isEnabled the enabled status
     * @return count of users
     */
	Long countByIsEnable(Boolean isEnable);
	
    /**
     * Check if a user exists by username
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
	boolean existsByUsername(String userName);
	
	/**
     * Check if a user exists by email
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */ 
	boolean existsByEmail(String email);
	
    /**
     * Find all enabled users with pagination
     * 
     * @param isEnabled the enabled status to filter by
     * @param pageable pagination information
     * @return page of users matching the criteria
     */
	Page<User> findByIsEnable(Boolean isEnable,Pageable pageable);
	
	/**
     * Find users by role name
     * 
     * @param roleName the name of the role
     * @param pageable pagination information
     * @return page of users with the specified role
     */
	
    /**
     * Find a user by username or email
     * 
     * @param usernameOrEmail to search for
     * @return Optional containing the user if found
     */
	@Query("Select u From User u Where u.username = :usernameOrEmail Or u.email = :usernameOrEmail")
	Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
	
	@Query("Select Distinct U From User U Join U.roles r Where r.name = :roleName ")
	Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

	/**
     * Search users by keyword in username, email, first name or last name
     * 
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
	@Query("Select u from User u Where "
			+ "Lower(u.username) Like Lower(Concat('%', :keyword, '%')) OR "
			+ "Lower(u.email) Like Lower(Concat('%', :keyword, '%')) OR "
			+ "Lower(u.firstName) Like Lower(Concat('%', :keyword, '%')) OR "
			+ "Lower(u.lastName) Like Lower(Concat('%', :keyword, '%')) ")
	Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
	
    /**
     * Update user's last login time
     * 
     * @param userId the user identifier
     * @param lastLoginAt the last login timestamp
     */
	@Modifying
	@Query("Update User U Set U.lastLoginAt = :lastLoginAt Where U.id = :id ")
	void updateLastLoginAt(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);
	
    /**
     * Find users created within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return page of users created within the date range
     */
	@Query("Select U From User U Where U.createdAt Between :startDate And :endDate")
	Page<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

}
