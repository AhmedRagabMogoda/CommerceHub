package com.commercehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {
	
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50,  message = "Username must be between 3 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscore and hyphen")
	private String username;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;
	
	@NotBlank(message = "Password is Required")
	@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
	         message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
	private String password;
	
	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String firstName;
	
	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	private String lastName;
	
	@Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Phone number format is invalid")
	@Size(min = 11, max = 11, message = "Phone number must be 11 characters")
	private String phoneNumber;

}
