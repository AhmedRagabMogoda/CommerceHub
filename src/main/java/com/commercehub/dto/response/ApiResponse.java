package com.commercehub.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper
 * Provides consistent response structure across all endpoints
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private Boolean success;
	
	private String message;
	
	private T data;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();
	
    /**
     * Create success response with data
     */
	public static <T> ApiResponse<T> success(T data)
	{
		return ApiResponse.<T>builder()
				.success(true)
				.data(data)
				.timestamp(LocalDateTime.now())
				.build();
	}
	
	/**
     * Create success response with message and data
     */
    public static <T> ApiResponse<T> success(String message, T data) 
    {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create success response with only message
     */
    public static <T> ApiResponse<T> success(String message) 
    {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create error response with message
     */
    public static <T> ApiResponse<T> error(T data)
    {
    	return ApiResponse.<T>builder()
    			.success(false)
    			.data(data)
    			.timestamp(LocalDateTime.now())
    			.build();
    }
    
    /**
     * Create error response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with message and data
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
	
}
