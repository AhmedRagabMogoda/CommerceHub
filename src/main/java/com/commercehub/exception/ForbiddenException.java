package com.commercehub.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user is authenticated but lacks permission
 * Returns HTTP 403 status (Forbidden)
 */

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) 
    {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public ForbiddenException(String message, Throwable cause) 
    {
        super(message, cause, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
