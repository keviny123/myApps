package com.keviny.customeridentity.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final String message;
    private final Map<String, String> details;

    public ErrorResponse(String message, Map<String, String> details) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public Map<String, String> getDetails() { return details; }
}