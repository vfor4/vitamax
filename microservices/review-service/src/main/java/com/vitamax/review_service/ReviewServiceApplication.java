package com.vitamax.review_service;

import com.vitamax.api.config.SwaggerConfig;
import com.vitamax.api.exception.GlobalControllerExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({GlobalControllerExceptionHandler.class, SwaggerConfig.class})
public class ReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }

}
