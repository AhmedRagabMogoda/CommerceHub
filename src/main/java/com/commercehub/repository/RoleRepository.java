package com.commercehub.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.commercehub.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role>{
	
    /**
     * Find a role by its name
     * 
     * @param name the role name to search for
     * @return Optional containing the role if found
     */
	Optional<Role> findByName(String name);

    /**
     * Check if a role exists by name
     * 
     * @param name the role name to check
     * @return true if role exists, false otherwise
     */
	boolean existsByName(String name);
	
    /**
     * Find roles by names
     * 
     * @param names set of role names to search for
     * @return set of roles matching the names
     */
	Set<Role> findByNameIn(Set<String> name);
	
    /**
     * Find all active roles with user count
     * 
     * @return set of roles with their user counts
     */
	@Query("Select r From Role r Left Join r.users u Group By r Order By Count(u) Desc")
	Set<Role> findAllRoleWithUserAcount();
	
    /**
     * Count users assigned to a specific role
     * 
     * @param roleId the role identifier
     * @return count of users with this role
     */
	@Query("Select u From User u Join u.roles r Where r.id = :roleId")
	Long countUsersByRoleId(@Param("roleId") Long roleId);
}
