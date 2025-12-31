package com.commercehub.util;

/**
 * JWT related constants
 */

public final class JwtConstants {
    private JwtConstants() {}

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_TYPE = "Bearer";
    public static final String AUTHORITIES_KEY = "authorities";
    public static final String USER_ID_KEY = "userId";
    public static final String USERNAME_KEY = "username";
    public static final long ACCESS_TOKEN_VALIDITY = 24 * 60 * 60 * 1000L; // 24 hours
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L; // 7 days
}