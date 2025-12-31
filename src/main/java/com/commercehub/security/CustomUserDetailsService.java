package com.commercehub.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commercehub.entity.User;
import com.commercehub.exception.ResourceNotFoundException;
import com.commercehub.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
    /**
     * Load user by username or email
     */
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException 
	{
		log.debug("Loading user by username or email: {}", usernameOrEmail);
		
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
				.orElseThrow( () -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
		
		return UserPrincipal.create(user);
	}
	
    /**
     * Load user by user ID
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) {
        log.debug("Loading user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return UserPrincipal.create(user);
    }

}
