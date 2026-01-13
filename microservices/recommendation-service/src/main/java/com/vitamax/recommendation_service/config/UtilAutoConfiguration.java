package com.vitamax.recommendation_service.config;

import com.vitamax.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilAutoConfiguration {

    @Bean
    public ServiceUtil serviceUtil(@Value("${server.port}") String port) {
        return new ServiceUtil(port);
    }
}
