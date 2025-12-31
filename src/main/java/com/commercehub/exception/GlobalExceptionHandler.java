package com.commercehub.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.commercehub.dto.response.ApiResponse;
import com.commercehub.dto.response.ErrorDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the application
 * Catches all exceptions and returns appropriate HTTP responses
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
    /**
     * Handle all custom base exceptions
     */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<ErrorDetails>> handleBaseException(BaseException ex, WebRequest request)
	{
		log.error("Base exception occurred: {}", ex.getMessage());
		
		ErrorDetails errorDetails = ErrorDetails.builder()
				.timestamp(LocalDateTime.now())
				.errorCode(ex.getErrorCode())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
		
		return new ResponseEntity<> (response, ex.getStatus());
	}
	
    /**
     * Handle ResourceNotFoundException
     */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<ErrorDetails>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request)
	{
		log.error("Resource Not Found Exception occured: {}", ex.getMessage());
		
		ErrorDetails errorDetails = ErrorDetails.builder()
				.timestamp(LocalDateTime.now())
				.errorCode(ex.getErrorCode())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("url=", ""))
				.build();
		ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
		
		return new ResponseEntity<> (response, HttpStatus.NOT_FOUND);		
	}
	
    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) 
    {
        log.error("Unauthorized access: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handle ForbiddenException
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleForbiddenException(ForbiddenException ex, WebRequest request) 
    {
        log.error("Forbidden access: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) 
    {
        log.error("Duplicate resource: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle DuplicateResourceException
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleBadRequestException(BadRequestException ex, WebRequest request) 
    {
        log.error("Bad Request: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle InsufficientStockException
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleInsufficientStockException(InsufficientStockException ex, WebRequest request) 
    {
        log.error("Insufficient Stock : {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        ApiResponse<ErrorDetails> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
