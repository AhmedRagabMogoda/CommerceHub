package com.commercehub.util;

/**
 * Security constants
 */
public final class Security {
	
    private Security() {}
    
    public static final int PASSWORD_ENCODER_STRENGTH = 12;
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long ACCOUNT_LOCK_DURATION_MINUTES = 30;
}
