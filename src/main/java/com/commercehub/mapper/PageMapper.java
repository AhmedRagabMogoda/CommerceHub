package com.commercehub.mapper;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.commercehub.dto.response.PageResponse;

/**
 * Helper component for converting Spring Page objects to PageResponse DTOs
 * Provides generic mapping functionality for paginated data
 */

@Component
public class PageMapper {
	
    /**
     * Convert Spring Page to PageResponse DTO
     * Applies mapping function to convert content items
     * 
     * @param page the Spring Page object
     * @param mapper function to convert individual items
     * @param <T> the source entity type
     * @param <R> the target DTO type
     * @return PageResponse containing converted items and pagination info
     */
	   public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) 
	   {
	        List<R> content = page.getContent().stream()
	                .map(mapper)
	                .toList();

	        return PageResponse.<R>builder()
	                .content(content)
	                .pageNumber(page.getNumber())
	                .pageSize(page.getSize())
	                .totalElements(page.getTotalElements())
	                .totalPages(page.getTotalPages())
	                .first(page.isFirst())
	                .last(page.isLast())
	                .empty(page.isEmpty())
	                .build();
	   }
	   
	    /**
	     * Convert Spring Page to PageResponse DTO using direct content list
	     * Use this when content is already mapped
	     * 
	     * @param page the Spring Page object
	     * @param content the already mapped content list
	     * @param <R> the DTO type
	     * @return PageResponse containing mapped items and pagination info
	     */
	    public <R> PageResponse<R> toPageResponse(Page<?> page, List<R> content) {
	        return PageResponse.<R>builder()
	                .content(content)
	                .pageNumber(page.getNumber())
	                .pageSize(page.getSize())
	                .totalElements(page.getTotalElements())
	                .totalPages(page.getTotalPages())
	                .first(page.isFirst())
	                .last(page.isLast())
	                .empty(page.isEmpty())
	                .build();
	    }
	    
}
