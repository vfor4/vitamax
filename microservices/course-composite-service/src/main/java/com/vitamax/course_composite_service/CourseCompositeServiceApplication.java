package com.vitamax.course_composite_service;

import com.vitamax.api_web_mvc.exception.GlobalControllerExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(GlobalControllerExceptionHandler.class)
public class CourseCompositeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseCompositeServiceApplication.class, args);
    }

}
