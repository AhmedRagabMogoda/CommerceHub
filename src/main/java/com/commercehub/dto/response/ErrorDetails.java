package com.commercehub.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetails {

    private LocalDateTime timestamp;

    private String errorCode;

    private String message;

    private String path;
}
