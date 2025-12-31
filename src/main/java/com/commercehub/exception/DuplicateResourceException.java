package com.commercehub.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists
 * Returns HTTP 409 status (Conflict)
 */

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
            HttpStatus.CONFLICT,
            "DUPLICATE_RESOURCE"
        );
    }

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}