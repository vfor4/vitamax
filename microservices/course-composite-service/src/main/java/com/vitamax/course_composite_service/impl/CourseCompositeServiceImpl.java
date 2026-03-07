package com.vitamax.course_composite_service.impl;

import com.vitamax.api.composite.course.CourseCompositeIntegration;
import com.vitamax.api.composite.course.CourseCompositeService;
import com.vitamax.api.composite.course.dto.CourseAggregate;
import com.vitamax.api.composite.course.dto.CourseAggregateCreateCommand;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.vitamax.api.constants.ServiceConstants.COURSE_COMPOSITE_URL;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseCompositeServiceImpl implements CourseCompositeService {
    private final CourseCompositeIntegration integrationService;

    @Override
    public Mono<CourseAggregate> getCourseComposite(final UUID courseId) {
        log.debug("get course composite for courseId={}", courseId);

        final var courseMono = integrationService.getCourse(courseId);

        final var recommendationsMono =
                integrationService.getRecommendations(courseId)
                        .collectList()
                        .onErrorReturn(Collections.emptyList());

        final var reviewsMono =
                integrationService.getReviews(courseId)
                        .collectList()
                        .onErrorReturn(Collections.emptyList());

        return Mono.zip(courseMono, recommendationsMono, reviewsMono)
                .map(t -> new CourseAggregate(
                        t.getT1(),
                        t.getT2(),
                        t.getT3()
                ))
                .doOnError(ex -> log.warn("Get course composite failed", ex));
    }

    @Override
    public Mono<Void> deleteCourseComposite(final UUID courseId) {
        log.debug("delete course composite for courseId={}", courseId);

        return Mono.when(
                integrationService.deleteCourse(courseId),
                integrationService.deleteRecommendations(courseId),
                integrationService.deleteReviews(courseId)
        ).then();
    }

    @Override
    public Mono<ResponseEntity<Void>> createCourseComposite(final CourseAggregateCreateCommand command,
                                                            final UriComponentsBuilder uriBuilder) {
        log.debug("create course composite for command={}", command);
        final var courseId = UUID.randomUUID();

        final var course = integrationService.createCourse(new CourseCreateCommand(courseId, command.course().name()));

        final var reviews = Flux.fromIterable(Objects.requireNonNullElse(command.reviews(), List.of()))
                .flatMap(rev -> integrationService.createReview(new ReviewCreateCommand(courseId, rev.author(), rev.subject(), rev.content())))
                .onErrorResume(e -> {
                    log.warn("Failed to create review, skipping: {}", e.getMessage());
                    return Mono.empty();
                });

        final var recommendations = Flux.fromIterable(Objects.requireNonNullElse(command.recommendations(), List.of()))
                .flatMap(red -> integrationService.createRecommendation(new RecommendationCreateCommand(courseId, red.author(), red.rate(), red.content())))
                .onErrorResume(e -> {
                    log.warn("Failed to create recommendation, skipping: {}", e.getMessage());
                    return Mono.empty();
                });

        return Mono.when(course, reviews, recommendations)
                .thenReturn(ResponseEntity.created(uriBuilder
                        .path(COURSE_COMPOSITE_URL + "/{courseId}")
                        .buildAndExpand(courseId)
                        .toUri()).build());
    }
}
