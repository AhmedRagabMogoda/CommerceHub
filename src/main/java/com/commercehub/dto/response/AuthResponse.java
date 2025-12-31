package com.commercehub.dto.response;
import com.commercehub.util.JwtConstants;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for authentication operations
 * Contains JWT tokens and basic user information
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tokenType = JwtConstants.TOKEN_PREFIX;

    private Long userId;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> roles;
}