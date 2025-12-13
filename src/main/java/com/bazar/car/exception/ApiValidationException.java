package com.bazar.car.exception;

import org.springframework.http.HttpStatus;

public class ApiValidationException extends RuntimeException {
    private final HttpStatus status;
    private final String error;
    private final String description;

    public ApiValidationException(HttpStatus status, String error, String description) {
        super(description);
        this.status = status;
        this.error = error;
        this.description = description;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}

