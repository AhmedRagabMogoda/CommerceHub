package com.commercehub.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AppConfig {
	
    /**
     * Configure AuditorAware for JPA auditing
     * Provides current user information for created_by and updated_by fields
     */
	@Bean
	AuditorAware<String> auditorProvider()
	{
		return new AuditorAware<String>() {
			
			@Override
			public Optional<String> getCurrentAuditor() 
			{
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				
				if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal()) )
					return Optional.of("system");
				
				return Optional.of(authentication.getName());
			}
		};
	}

}
