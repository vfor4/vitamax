package com.vitamax.course_composite_service;

import com.vitamax.api.config.SwaggerConfig;
import com.vitamax.api.exception.GlobalControllerExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({GlobalControllerExceptionHandler.class, SwaggerConfig.class})
public class CourseCompositeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseCompositeServiceApplication.class, args);
    }

}
