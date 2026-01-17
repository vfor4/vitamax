package com.vitamax.course_composite_service.course_composite;

import com.vitamax.composite.course.CourseAggregate;
import com.vitamax.composite.course.CourseCompositeIntegration;
import com.vitamax.composite.course.CourseCompositeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseCompositeServiceImpl implements CourseCompositeService {
    private final CourseCompositeIntegration courseCompositeIntegration;

    @Override
    public ResponseEntity<CourseAggregate> getCourseComposite(final int courseId) {
        log.debug("get course composite for courseId={}", courseId);

        final var course = courseCompositeIntegration.getCourse(courseId);
        final var recommendation = courseCompositeIntegration.getRecommendations(courseId);
        final var review = courseCompositeIntegration.getReviews(courseId);

        return ResponseEntity.ok(new CourseAggregate(course.getBody(), recommendation.getBody(), review.getBody()));
    }

    @Override
    public ResponseEntity<Void> deleteCourseComposite(final int courseId) {
        log.debug("delete course composite for courseId={}", courseId);

        courseCompositeIntegration.deleteCourse(courseId);
        courseCompositeIntegration.deleteRecommendation(courseId);
        courseCompositeIntegration.deleteReview(courseId);

        return ResponseEntity.noContent().build();
    }
}
