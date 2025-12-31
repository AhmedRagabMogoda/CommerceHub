package com.commercehub.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a request is invalid or malformed
 * Returns HTTP 400 status
 */

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }
}