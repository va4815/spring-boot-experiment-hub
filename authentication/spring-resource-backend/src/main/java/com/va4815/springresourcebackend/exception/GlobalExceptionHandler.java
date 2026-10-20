package com.va4815.springresourcebackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ResponseErrorObject error) {
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(GlobalArgumentException.class)
    public ResponseEntity<Object> handleGlobalArgumentException(GlobalArgumentException ex) {
        ResponseErrorObject error = new ResponseErrorObject(ex.getMessage(), ex.getStatus(), ex);
        return buildResponseEntity(error);
    }

}
