package com.vitamax.course_composite_service.config;

import com.vitamax.course_composite_service.impl.CourseCompositeHealthService;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.vitamax.course_composite_service.constants.ServiceConstants.COURSE_HOST;
import static com.vitamax.course_composite_service.constants.ServiceConstants.RECOMMENDATION_HOST;
import static com.vitamax.course_composite_service.constants.ServiceConstants.REVIEW_HOST;

@Configuration
public class ActuatorHealthConfiguration {

    private final CourseCompositeHealthService service;

    public ActuatorHealthConfiguration(final CourseCompositeHealthService service) {
        this.service = service;
    }

    @Bean
    public CompositeReactiveHealthContributor healthContributor() {
        final Map<String, ReactiveHealthIndicator> map = new LinkedHashMap<>();

        map.put("course", () -> service.getActuatorHealth(COURSE_HOST));
        map.put("recommendation", () -> service.getActuatorHealth(RECOMMENDATION_HOST));
        map.put("review", () -> service.getActuatorHealth(REVIEW_HOST));
        return CompositeReactiveHealthContributor.fromMap(map);
    }
}
