package ru.practicum.ewm.handler;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> numberFormatError(final NumberFormatException e) {
        log.info("400 {}", e.getMessage());
        return Map.of("message", e.getMessage(),
                "reason", "Incorrectly made request.",
                "status", "BAD_REQUEST",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequestError(final ValidationException e) {
        log.info("400 {}", e.getMessage());
        return Map.of("message", e.getMessage(),
                "reason", e.getReason(),
                "status", e.getStatus().name(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflictError(final ConflictException e) {
        log.info("409 {}", e.getMessage());
        return Map.of("message", e.getMessage(),
                "reason", e.getReason(),
                "status", e.getStatus().name(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundError(final NotFoundException e) {
        log.info("404 {}", e.getMessage());
        return Map.of("message", e.getMessage(),
                "reason", e.getReason(),
                "status", e.getStatus().name(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage());
        String[] errorString = e.getMessage().split(";");
        String shortErrorString = errorString[errorString.length - 2];
        String error = shortErrorString.substring(shortErrorString.indexOf('[') + 1, shortErrorString.indexOf(']'));
        return Map.of("message", "Field: "+ error + ". Error: must not be blank. Value: null",
                "reason", "Incorrectly made request.",
                "status", HttpStatus.BAD_REQUEST.name(),
                "timestamp", LocalDateTime.now().toString()
        );
    }
}