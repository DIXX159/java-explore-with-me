package ru.practicum.explorewithme.error;

public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}