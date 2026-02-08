package com.vitamax.course_composite_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@Slf4j
public class EventConfiguration {

    @Bean
    Scheduler publishScheduler() {
        log.info("Create publish scheduler with pool size: {} and queue size: {}", 10, 100);
        return Schedulers.newBoundedElastic(10, 100, "course-publish-scheduler");
    }

}
