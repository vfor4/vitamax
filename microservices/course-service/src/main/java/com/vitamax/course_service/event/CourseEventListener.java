package com.vitamax.course_service.event;

import com.vitamax.api.core.course.CourseService;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CourseEventListener {

    private final CourseService courseService;

    @Bean
    public Consumer<Flux<CourseCreateCommand>> courseCreate() {
        return flux -> flux
                .doOnNext(cmd -> log.info("Received command: {}", cmd))
                .flatMap(courseService::createCourse)
                .doOnError(e -> log.error("Failed to process course event", e))
                .subscribe(
                        null,
                        e -> {}
                );
    }

    @Bean
    public Consumer<Flux<CourseUpdateCommand>> courseUpdate() {
        return flux -> flux
                .doOnNext(cmd -> log.info("Received command: {}", cmd))
                .flatMap(courseService::updateCourse)
                .doOnError(e -> log.error("Failed to process course update event", e))
                .subscribe(
                        null,
                        e -> {}
                );
    }

    @Bean
    public Consumer<Flux<UUID>> courseDelete() {
        return flux -> flux
                .doOnNext(uuid -> log.info("Received uuid: {}", uuid))
                .flatMap(courseService::deleteCourse)
                .doOnError(e -> log.error("Failed to process course delete event", e))
                .subscribe(
                        null,
                        e -> {}
                );
    }
}
