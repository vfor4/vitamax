package com.vitamax.course_composite_service.impl;

import com.vitamax.api.composite.course.CourseCompositeIntegration;
import com.vitamax.api.composite.course.CourseCompositeService;
import com.vitamax.api.composite.course.dto.CourseAggregate;
import com.vitamax.api.composite.course.dto.CourseAggregateCreateCommand;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.exception.dto.NotFoundException;
import com.vitamax.api.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseCompositeServiceImpl implements CourseCompositeService {
    private final CourseCompositeIntegration integrationService;

    @Override
    public Mono<CourseAggregate> getCourseComposite(final UUID courseId) {
        log.debug("get course composite for courseId={}", courseId);

        final var courseMono =
                integrationService.getCourse(courseId)
                        .switchIfEmpty(Mono.error(new NotFoundException("course not found with id " + courseId)));
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
    public ResponseEntity<Void> deleteCourseComposite(final UUID courseId) {
        log.debug("delete course composite for courseId={}", courseId);

        integrationService.deleteCourse(courseId);
        integrationService.deleteRecommendations(courseId);
        integrationService.deleteReviews(courseId);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Void> createCourseComposite(final CourseAggregateCreateCommand command) {
        log.debug("create course composite for command={}", command);
        final var courseId = UUID.randomUUID();
        integrationService.createCourse(new CourseCreateCommand(courseId, command.course().name())).subscribe();

        if (command.reviews() != null) {
            command.reviews().forEach(review ->
                    integrationService.createReview(new ReviewCreateCommand(courseId, review.author(), review.subject(), review.content())).subscribe());
        }
        if (command.recommendations() != null) {
            command.recommendations().forEach(
                    red -> integrationService.createRecommendation(new RecommendationCreateCommand(courseId, red.author(), red.rate(), red.content())).subscribe()
            );
        }

        return ResponseEntity.created(ApiUtil.buildCreatedLocation(courseId)).build();
    }
}
