package com.vitamax.course_composite_service.config;

import com.vitamax.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UtilAutoConfiguration {
    @Bean
    public ServiceUtil serviceUtil(@Value("${server.port}") String port) {
        return new ServiceUtil(port);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}