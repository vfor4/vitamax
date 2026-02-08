package com.vitamax.course_composite_service.impl;

import com.vitamax.api.composite.course.CourseCompositeIntegration;
import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.api.core.review.dto.Review;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.course_composite_service.config.ServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.UUID;

import static com.vitamax.api.event.EventConstants.COURSE_QUEUE_NAME;
import static com.vitamax.api.event.EventConstants.RECOMMENDATION_QUEUE_NAME;
import static com.vitamax.api.event.EventConstants.REVIEW_QUEUE_NAME;

@Service
@Slf4j
public class CourseCompositeIntegrationImpl implements CourseCompositeIntegration {
    private static final String API_COURSE = "/api/v1/course";
    private static final String API_RECOMMENDATION = "/api/v1/recommendation";
    private static final String API_REVIEW = "/api/v1/review";
    private static final String COURSE = "course";
    private static final String RECOMMENDATION = "recommendation";
    private static final String REVIEW = "review";
    private static final String COURSE_ID = "/{courseId}";

    private final WebClient webClient;
    private final RabbitTemplate rabbitTemplate;
    private final ServiceProperties properties;
    private final Scheduler publishScheduler;

    public CourseCompositeIntegrationImpl(final RabbitTemplate rabbitTemplate, final ServiceProperties properties, final Scheduler publishScheduler) {
        this.rabbitTemplate = rabbitTemplate;
        this.publishScheduler = publishScheduler;
        this.webClient = WebClient.builder().build();
        this.properties = properties;
    }

    @Override
    public Mono<Course> getCourse(final UUID courseId) {
        log.info("getCourse {}", courseId);
        final var url = properties.toUrl(COURSE) + API_COURSE + COURSE_ID;
        return webClient.get().uri(url, courseId).retrieve().bodyToMono(Course.class);
    }

    @Override
    public Mono<Void> createCourse(final CourseCreateCommand command) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(COURSE_QUEUE_NAME, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> updateCourse(final CourseUpdateCommand command) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(COURSE_QUEUE_NAME, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> deleteCourse(final UUID courseId) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(COURSE_QUEUE_NAME, courseId.toString()))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Flux<Recommendation> getRecommendations(final UUID courseId) {
        log.info("getRecommendations {}", courseId);
        final var url = properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION + COURSE_ID;
        return webClient.get().uri(url, courseId).retrieve().bodyToFlux(Recommendation.class);
    }

    @Override
    public Mono<Void> createRecommendation(final RecommendationCreateCommand command) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(RECOMMENDATION_QUEUE_NAME, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Recommendation> updateRecommendation(final RecommendationUpdateCommand command) {
//        return webClient.exchange(properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION, HttpMethod.PUT, new HttpEntity<>(command), Recommendation.class);
        return null;
    }

    @Override
    public Mono<Void> deleteRecommendations(final UUID courseId) {
//        return webClient.exchange(properties.toUrl(RECOMMENDATION) + API_RECOMMENDATION + COURSE_ID, HttpMethod.DELETE, null, Void.class, String.valueOf(courseId));
        return null;
    }

    @Override
    public Flux<Review> getReviews(final UUID courseId) {
        log.info("getReviews {}", courseId);
        final var url = properties.toUrl(REVIEW) + API_REVIEW + COURSE_ID;
        return webClient.get().uri(url, courseId).retrieve().bodyToFlux(Review.class);
    }

    @Override
    public Mono<Void> createReview(final ReviewCreateCommand command) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(REVIEW_QUEUE_NAME, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Review> updateReview(final ReviewUpdateCommand command) {
//        return webClient.exchange(properties.toUrl(REVIEW) + API_REVIEW, HttpMethod.PUT, new HttpEntity<>(command), Review.class);
        return null;
    }

    @Override
    public Mono<Void> deleteReviews(final UUID courseId) {
//        return webClient.exchange(properties.toUrl(REVIEW) + API_REVIEW + COURSE_ID, HttpMethod.DELETE, null, Void.class, String.valueOf(courseId));
        return null;
    }
}
