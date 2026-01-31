package com.vitamax.course_service.config;

import com.vitamax.api.exception.GlobalControllerExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CourseControllerExceptionHandler extends GlobalControllerExceptionHandler {
}
