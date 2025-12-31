package com.commercehub.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic paginated response wrapper
 * Provides consistent pagination information across all paginated endpoints
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

	private List<T> content;
	
    private int pageNumber;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;

    private boolean empty;

}
