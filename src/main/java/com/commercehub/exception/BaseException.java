package com.commercehub.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Base exception class for all custom exceptions
 * Provides common fields and behavior for exception handling
 */

@Getter
public abstract class BaseException extends RuntimeException{

	private final HttpStatus status;
	
	private final String errorCode;
	
	protected BaseException(String message, HttpStatus status, String errorcode)
	{
		super(message);
		this.status = status;
		this.errorCode = errorcode;
	}
	
    protected BaseException(String message, Throwable cause, HttpStatus status, String errorCode) 
    {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
