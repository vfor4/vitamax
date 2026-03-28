package com.vitamax.gateway_service.config;

import com.vitamax.gateway_service.actuator.CourseCompositeHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.vitamax.util.constants.ServiceConstants.COURSE_HOST;
import static com.vitamax.util.constants.ServiceConstants.RECOMMENDATION_HOST;
import static com.vitamax.util.constants.ServiceConstants.REVIEW_HOST;

@Configuration
@RequiredArgsConstructor
public class ActuatorHealthConfiguration {

    private final CourseCompositeHealthService service;

    @Bean
    public CompositeReactiveHealthContributor healthContributor() {
        final Map<String, ReactiveHealthIndicator> map = new LinkedHashMap<>();

        map.put("course", () -> service.getActuatorHealth(COURSE_HOST));
        map.put("recommendation", () -> service.getActuatorHealth(RECOMMENDATION_HOST));
        map.put("review", () -> service.getActuatorHealth(REVIEW_HOST));
        return CompositeReactiveHealthContributor.fromMap(map);
    }
}
