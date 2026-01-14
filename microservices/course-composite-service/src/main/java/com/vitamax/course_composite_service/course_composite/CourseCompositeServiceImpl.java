package com.vitamax.course_composite_service.course_composite;

import com.vitamax.composite.course.CourseAggregate;
import com.vitamax.composite.course.CourseCompositeIntegration;
import com.vitamax.composite.course.CourseCompositeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseCompositeServiceImpl implements CourseCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseCompositeServiceImpl.class);

    private final CourseCompositeIntegration courseCompositeIntegration;

    public CourseCompositeServiceImpl(CourseCompositeIntegration courseCompositeIntegration) {
        this.courseCompositeIntegration = courseCompositeIntegration;
    }

    @Override
    public CourseAggregate getCourseComposite(int courseId) {
        LOG.debug("get course composite for courseId={}", courseId);
        final var course = courseCompositeIntegration.getCourse(courseId);
        final var recommendation = courseCompositeIntegration.getRecommendations(courseId);
        final var review = courseCompositeIntegration.getReviews(courseId);
        return new CourseAggregate(course, recommendation, review);
    }
}
