package com.vitamax.course_composite_service.course_composite;

import com.vitamax.composite.course.CourseCompositeIntegration;
import com.vitamax.core.course.Course;
import com.vitamax.core.course.CourseCreateCommand;
import com.vitamax.core.course.CourseUpdateCommand;
import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.recommendation.RecommendationCreateCommand;
import com.vitamax.core.recommendation.RecommendationUpdateCommand;
import com.vitamax.core.review.Review;
import com.vitamax.core.review.ReviewCreateCommand;
import com.vitamax.core.review.ReviewUpdateCommand;
import com.vitamax.course_composite_service.config.ServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseCompositeIntegrationImpl implements CourseCompositeIntegration {
    private static final String API_COURSE = "/api/v1/course";
    private static final String API_RECOMMENDATION = "/api/v1/recommendation";
    private static final String API_REVIEW = "/api/v1/review";
    private static final String COURSE = "course";
    private static final String RECOMMENDATION = "recommendation";
    private static final String REVIEW = "review";
    private static final String COURSE_ID = "/{courseId}";

    private final RestTemplate restTemplate;

    private final ServiceProperties properties;

    @Override
    public ResponseEntity<Course> getCourse(final UUID courseId) {
        return restTemplate.exchange(properties.toUrl(COURSE) + API_COURSE + COURSE_ID, HttpMethod.GET, null, Course.class, String.valueOf(courseId));
    }

    @Override
    public ResponseEntity<Course> createCourse(final CourseCreateCommand command) {
        return restTemplate.exchange(properties.toUrl(COURSE)+ API_COURSE, HttpMethod.POST, new HttpEntity<>(command), Course.class);
    }

    @Override
    public ResponseEntity<Course> updateCourse(final CourseUpdateCommand command) {
        return restTemplate.exchange(properties.toUrl(COURSE) + API_COURSE, HttpMethod.PUT, new HttpEntity<>(command), Course.class);
    }

    @Override
    public ResponseEntity<Void> deleteCourse(final UUID courseId) {
        return restTemplate.exchange(properties.toUrl(COURSE) + API_COURSE + COURSE_ID, HttpMethod.DELETE, null, Void.class, String.valueOf(courseId));
    }

    @Override
    public ResponseEntity<List<Recommendation>> getRecommendations(final UUID courseId) {
        try {
            final var recommendations = restTemplate.exchange(properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION + COURSE_ID, HttpMethod.GET, null, Recommendation[].class, String.valueOf(courseId));
            return ResponseEntity.of(Optional.ofNullable(recommendations.getBody()).map(r -> Arrays.stream(r).toList()));
        } catch (final RestClientException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @Override
    public ResponseEntity<Recommendation> createRecommendation(final RecommendationCreateCommand command) {
        return restTemplate.exchange(properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION, HttpMethod.POST, new HttpEntity<>(command), Recommendation.class);
    }

    @Override
    public ResponseEntity<Recommendation> updateRecommendation(RecommendationUpdateCommand command) {
        return restTemplate.exchange(properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION, HttpMethod.PUT, new HttpEntity<>(command), Recommendation.class);
    }

    @Override
    public ResponseEntity<Void> deleteRecommendations(final UUID courseId) {
        return restTemplate.exchange(properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION + COURSE_ID, HttpMethod.DELETE, null, Void.class, String.valueOf(courseId));
    }

    @Override
    public ResponseEntity<List<Review>> getReviews(final UUID courseId) {
        try {
            final var reviews = restTemplate.exchange(properties.toUrl(REVIEW) + API_REVIEW + COURSE_ID, HttpMethod.GET, null, Review[].class, String.valueOf(courseId));
            return ResponseEntity.of(Optional.ofNullable(reviews.getBody()).map(r -> Arrays.stream(r).toList()));
        } catch (final RestClientException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @Override
    public ResponseEntity<Review> createReview(final ReviewCreateCommand command) {
        return restTemplate.exchange(properties.toUrl(REVIEW) + API_REVIEW, HttpMethod.POST, new HttpEntity<>(command), Review.class);
    }

    @Override
    public ResponseEntity<Review> updateReview(final ReviewUpdateCommand command) {
        return restTemplate.exchange(properties.toUrl(REVIEW) + API_REVIEW, HttpMethod.PUT, new HttpEntity<>(command), Review.class);
    }

    @Override
    public ResponseEntity<Void> deleteReviews(final UUID courseId) {
        return restTemplate.exchange(properties.toUrl(REVIEW) + API_REVIEW + COURSE_ID, HttpMethod.DELETE, null, Void.class, String.valueOf(courseId));
    }
}
