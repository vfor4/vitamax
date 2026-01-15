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
import com.vitamax.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CourseCompositeIntegrationImpl implements CourseCompositeIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(CourseCompositeIntegrationImpl.class);

    public static final String API_COURSE = "http://localhost:{port}/api/v1/course";
    public static final String API_RECOMMENDATION = "http://localhost:{port}/api/v1/recommendation";
    public static final String API_REVIEW = "http://localhost:{port}/api/v1/review";

    private final RestTemplate restTemplate;

    @Qualifier(value = "courseServiceUtil")
    private final ServiceUtil courseServiceUtil;

    @Qualifier(value = "recommendationServiceUtil")
    private final ServiceUtil recommendationServiceUtil;

    @Qualifier(value = "reviewServiceUtil")
    private final ServiceUtil reviewServiceUtil;

    public CourseCompositeIntegrationImpl(RestTemplate restTemplate, ServiceUtil courseServiceUtil, ServiceUtil recommendationServiceUtil, ServiceUtil reviewServiceUtil) {
        this.restTemplate = restTemplate;
        this.courseServiceUtil = courseServiceUtil;
        this.recommendationServiceUtil = recommendationServiceUtil;
        this.reviewServiceUtil = reviewServiceUtil;
    }

    @Override
    public ResponseEntity<Course> getCourse(final int courseId) {
        return restTemplate.exchange(API_COURSE + "/{courseId}", HttpMethod.GET, null, Course.class, courseServiceUtil.getPort(), String.valueOf(courseId));
    }

    @Override
    public ResponseEntity<Course> createCourse(final CourseCreateCommand command) {
        return restTemplate.exchange(API_COURSE, HttpMethod.POST, new HttpEntity<>(command), Course.class, courseServiceUtil.getPort());
    }

    @Override
    public ResponseEntity<Course> updateCourse(final CourseUpdateCommand command) {
        return restTemplate.exchange(API_COURSE, HttpMethod.PUT,  new HttpEntity<>(command), Course.class, courseServiceUtil.getPort());
    }

    @Override
    public ResponseEntity<Void> deleteCourse(final int courseId) {
        return restTemplate.exchange(API_COURSE + "/{courseId}", HttpMethod.DELETE,  null, Void.class, courseServiceUtil.getPort(), String.valueOf(courseId));
    }

    @Override
    public ResponseEntity<List<Recommendation>> getRecommendations(final int courseId) {
        try {
            final var recommendations = restTemplate.exchange(API_RECOMMENDATION + "/{courseId}", HttpMethod.GET, null, Recommendation[].class, recommendationServiceUtil.getPort(), String.valueOf(courseId));
            return ResponseEntity.of(Optional.ofNullable(recommendations.getBody()).map(r -> Arrays.stream(r).toList()));
        } catch (final RestClientException e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @Override
    public ResponseEntity<Recommendation> createRecommendation(final RecommendationCreateCommand command) {
        return restTemplate.exchange(API_RECOMMENDATION, HttpMethod.POST, new HttpEntity<>(command), Recommendation.class, recommendationServiceUtil.getPort());
    }

    @Override
    public ResponseEntity<Recommendation> updateRecommendation(RecommendationUpdateCommand command) {
        return restTemplate.exchange(API_RECOMMENDATION, HttpMethod.PUT,  new HttpEntity<>(command), Recommendation.class, recommendationServiceUtil.getPort());
    }

    @Override
    public ResponseEntity<Void> deleteRecommendation(int courseId) {
        return restTemplate.exchange(API_RECOMMENDATION + "/{courseId}", HttpMethod.DELETE,  null, Void.class, recommendationServiceUtil.getPort(), String.valueOf(courseId));
    }

    @Override
    public ResponseEntity<List<Review>> getReviews(int courseId) {
        try {
            final var reviews = restTemplate.exchange(API_REVIEW + "/{courseId}", HttpMethod.GET, null, Review[].class, reviewServiceUtil.getPort(), String.valueOf(courseId));
            return ResponseEntity.of(Optional.ofNullable(reviews.getBody()).map(r -> Arrays.stream(r).toList()));
        } catch (final RestClientException e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @Override
    public ResponseEntity<Review> createReview(ReviewCreateCommand command) {
        return restTemplate.exchange(API_REVIEW, HttpMethod.POST, new HttpEntity<>(command), Review.class, reviewServiceUtil.getPort());
    }

    @Override
    public ResponseEntity<Review> updateReview(ReviewUpdateCommand command) {
        return restTemplate.exchange(API_REVIEW, HttpMethod.PUT,  new HttpEntity<>(command), Review.class, reviewServiceUtil.getPort());
    }

    @Override
    public ResponseEntity<Void> deleteReview(int courseId) {
        return restTemplate.exchange(API_REVIEW + "/{courseId}", HttpMethod.DELETE,  null, Void.class, reviewServiceUtil.getPort(), String.valueOf(courseId));
    }
}
