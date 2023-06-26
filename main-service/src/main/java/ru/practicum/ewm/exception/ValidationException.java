package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ValidationException extends Exception {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    private LocalDateTime timestamp = LocalDateTime.now();


    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ValidationException(String message, String reason, HttpStatus status) {
        this.message = message;
        this.reason = reason;
        this.status = status;
    }
}