package com.commercehub.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user is not authorized to access a resource
 * Returns HTTP 401 status
 */

public class UnauthorizedException extends BaseException {

	public UnauthorizedException(String message) 
	{
		super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}
	
	public UnauthorizedException(String message,Throwable cause) 
	{
		super(message, cause, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}

}
