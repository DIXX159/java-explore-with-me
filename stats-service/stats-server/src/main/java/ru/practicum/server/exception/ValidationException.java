package ru.practicum.server.exception;

public class ValidationException extends Exception {
    private final String parameter;

    public ValidationException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}