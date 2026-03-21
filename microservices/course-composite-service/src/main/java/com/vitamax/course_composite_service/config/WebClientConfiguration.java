package com.vitamax.course_composite_service.config;

import com.vitamax.auth_service.JwtProperties;
import com.vitamax.auth_service.JwtUtil;
import com.vitamax.auth_service.ServiceAuthFilter;
import com.vitamax.auth_service.ServiceTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
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

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder()
                .filter(serviceTokenFilter(jwtUtil(), jwtProperties()).authFilter())
                .filter(errorHandlingFilter());
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return Mono.error(new RuntimeException("Service unauthorized"));
            }
            if (response.statusCode().equals(HttpStatus.FORBIDDEN)) {
                return Mono.error(new RuntimeException("Service forbidden"));
            }
            if (response.statusCode().is5xxServerError()) {
                return Mono.error(new RuntimeException("Downstream error: " + response.statusCode()));
            }
            return Mono.just(response);
        });
    }
}
