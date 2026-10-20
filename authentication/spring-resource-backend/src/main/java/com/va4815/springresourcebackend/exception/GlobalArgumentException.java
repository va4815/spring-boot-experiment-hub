package com.va4815.springresourcebackend.exception;

import org.springframework.http.HttpStatus;

public class GlobalArgumentException extends RuntimeException {
    private final HttpStatus status;

    public GlobalArgumentException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
