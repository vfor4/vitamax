package com.vitamax.course_service.event;

import com.vitamax.api.core.course.CourseService;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CourseEventListener {

    private final CourseService courseService;

    @Bean
    public Consumer<Flux<CourseCreateCommand>> course() {
        return flux -> flux
                .flatMap(courseService::createCourse)
                .doOnError(e -> log.error("Failed to process course event", e))
                .retry()
                .subscribe();
    }
}
