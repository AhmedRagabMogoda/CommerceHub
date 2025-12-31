package com.commercehub.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to order more items than available in stock
 * Returns HTTP 400 status (Bad Request)
 */

public class InsufficientStockException extends BaseException {

    public InsufficientStockException(String productName, int requested, int available) 
    {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d", productName, requested, available),
              HttpStatus.BAD_REQUEST,
              "INSUFFICIENT_STOCK"
             );
    }

    public InsufficientStockException(String message) 
    {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(String message, Throwable cause) 
    {
        super(message, cause, HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }
}
