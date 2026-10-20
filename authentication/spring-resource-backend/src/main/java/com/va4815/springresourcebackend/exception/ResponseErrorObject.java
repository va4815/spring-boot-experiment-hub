package com.va4815.springresourcebackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseErrorObject {
    private String title;
    private String detail;
    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private ResponseErrorObject() {
        timestamp = LocalDateTime.now();
    }


    ResponseErrorObject(String title, HttpStatus status, Throwable ex) {
        this();
        this.title = title;
        this.status = status;
        this.detail = ex.getLocalizedMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDetail() {
        return detail;
    }

    public String getTitle() {
        return title;
    }

}
