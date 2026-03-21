package com.vitamax.course_composite_service.config;

import com.vitamax.auth_service.JwtProperties;
import com.vitamax.auth_service.JwtUtil;
import com.vitamax.auth_service.ServiceAuthFilter;
import com.vitamax.auth_service.ServiceTokenFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfiguration {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    @ConfigurationProperties(prefix = "jwt")
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public ServiceTokenFilter serviceTokenFilter(JwtUtil jwtUtil, JwtProperties jwtProperties) {
        return new ServiceTokenFilter(jwtUtil, jwtProperties);
    }

    @Bean
    public ServiceAuthFilter serviceAuthFilter(JwtUtil jwtUtil) {
        return new ServiceAuthFilter(jwtUtil);
    }
}
