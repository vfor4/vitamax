package com.vitamax.recommendation_service;

import com.vitamax.api_web_mvc.exception.GlobalControllerExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@Import(GlobalControllerExceptionHandler.class)
public class RecommendationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationServiceApplication.class, args);
    }

}
