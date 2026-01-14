package com.vitamax.course_composite_service.course_composite;

import com.vitamax.composite.course.CourseCompositeIntegration;
import com.vitamax.core.course.Course;
import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.review.Review;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CourseCompositeIntegrationImpl implements CourseCompositeIntegration {
    private final RestTemplate restTemplate;

    public CourseCompositeIntegrationImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Course getCourse(int courseId) {
        return restTemplate.getForObject("http://localhost:7002/course/" + courseId, Course.class);
    }

    @Override
    public List<Recommendation> getRecommendations(int courseId) {
        final var recommendations = restTemplate.getForObject("http://localhost:7003/recommendation/" + courseId, Recommendation[].class);
        if (recommendations == null) {
            return List.of();
        }
        return List.of(recommendations);
    }

    @Override
    public List<Review> getReviews(int courseId) {
        final var reviews = restTemplate.getForObject("http://localhost:7004/review/" + courseId, Review[].class);
        if (reviews == null) {
            return List.of();
        }
        return List.of(reviews);
    }
}
