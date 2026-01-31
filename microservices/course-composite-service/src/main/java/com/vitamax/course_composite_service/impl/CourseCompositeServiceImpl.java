package com.vitamax.course_composite_service.impl;

import com.vitamax.api.composite.course.CourseCompositeIntegration;
import com.vitamax.api.composite.course.CourseCompositeService;
import com.vitamax.api.composite.course.dto.CourseAggregate;
import com.vitamax.api.composite.course.dto.CourseAggregateCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseCompositeServiceImpl implements CourseCompositeService {
    private final CourseCompositeIntegration courseCompositeIntegration;

    @Override
    public ResponseEntity<CourseAggregate> getCourseComposite(final UUID courseId) {
        log.debug("get course composite for courseId={}", courseId);

        final var course = courseCompositeIntegration.getCourse(courseId);
        final var recommendation = courseCompositeIntegration.getRecommendations(courseId);
        final var review = courseCompositeIntegration.getReviews(courseId);

        return ResponseEntity.ok(new CourseAggregate(course.getBody(), recommendation.getBody(), review.getBody()));
    }

    @Override
    public ResponseEntity<Void> deleteCourseComposite(final UUID courseId) {
        log.debug("delete course composite for courseId={}", courseId);

        courseCompositeIntegration.deleteCourse(courseId);
        courseCompositeIntegration.deleteRecommendations(courseId);
        courseCompositeIntegration.deleteReviews(courseId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> createCourseComposite(final CourseAggregateCreateCommand command) {
        log.debug("create course composite for command={}", command);

        final var courseId = ApiUtil.extractIdFromHeader(courseCompositeIntegration.createCourse(command.course()).getHeaders());

        if (command.reviews() != null) {
            command.reviews().forEach(review ->
                    courseCompositeIntegration.createReview(new ReviewCreateCommand(UUID.fromString(courseId), review.author(), review.subject(), review.content())));
        }
        if (command.recommendations() != null) {
            command.recommendations().forEach(
                    red -> courseCompositeIntegration.createRecommendation(new RecommendationCreateCommand(UUID.fromString(courseId), red.author(), red.rate(), red.content()))
            );
        }

        return ResponseEntity.created(ApiUtil.buildCreatedLocation(courseId)).build();
    }
}
