package com.vitamax.api.exception.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record HttpErrorInfo(Instant timestamp, String path, HttpStatus httpStatus, String message) {
    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this(Instant.now(), path, httpStatus, message);
    }
}
