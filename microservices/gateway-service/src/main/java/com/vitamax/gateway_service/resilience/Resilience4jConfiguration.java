package com.vitamax.gateway_service.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfiguration {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> compositeCircuitBreakerConfiguration() {
        final var recordException = new RecordExceptionPredicate();
        return factory ->
                factory.configure(builder -> builder
                        .circuitBreakerConfig(CircuitBreakerConfig
                                .custom()
                                .slidingWindowSize(10)
                                .minimumNumberOfCalls(10)
                                .permittedNumberOfCallsInHalfOpenState(3)
                                .waitDurationInOpenState(Duration.ofSeconds(5))
                                .failureRateThreshold(60)
                                .recordException(recordException)
                                .build()
                        )
                        .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build())
                        .build(), "course-composite-circuit-breaker");
    }
}
