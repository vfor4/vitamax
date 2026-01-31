package com.vitamax.review_service.config;

import com.vitamax.api.exception.GlobalControllerExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReviewControllerExceptionHandler extends GlobalControllerExceptionHandler {
}
