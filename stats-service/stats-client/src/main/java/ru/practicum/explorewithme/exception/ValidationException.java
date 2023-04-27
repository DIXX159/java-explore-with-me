package ru.practicum.explorewithme.exception;

public class ValidationException extends Exception {
    private final String parameter;

    public ValidationException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}