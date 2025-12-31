package com.commercehub.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found
 * Returns HTTP 404 status
 */

public class ResourceNotFoundException extends BaseException {

	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) 
	{
		super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
			  HttpStatus.NOT_FOUND,
			  "RESOURCE_NOT_FOUND" );
	}

    public ResourceNotFoundException(String message) 
    {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message, Throwable cause) 
    {
        super(message, cause, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

}
