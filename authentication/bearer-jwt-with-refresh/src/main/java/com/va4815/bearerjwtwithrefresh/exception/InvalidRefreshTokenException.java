package com.va4815.bearerjwtwithrefresh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRefreshTokenException extends ResponseStatusException {

    private static final String MESSAGE = "Invalid refresh token";

    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
