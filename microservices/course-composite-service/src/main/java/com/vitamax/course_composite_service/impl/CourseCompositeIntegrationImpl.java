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
import com.vitamax.api.exception.dto.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.UUID;

import static com.vitamax.api.event.EventConstants.COURSE_CREATE_OUT_0;
import static com.vitamax.api.event.EventConstants.COURSE_DELETE_OUT_0;
import static com.vitamax.api.event.EventConstants.COURSE_UPDATE_OUT_0;
import static com.vitamax.api.event.EventConstants.RECOMMENDATION_CREATE_OUT_0;
import static com.vitamax.api.event.EventConstants.RECOMMENDATION_DELETE_OUT_0;
import static com.vitamax.api.event.EventConstants.RECOMMENDATION_UPDATE_OUT_0;
import static com.vitamax.api.event.EventConstants.REVIEW_CREATE_OUT_0;
import static com.vitamax.util.constants.ServiceConstants.COURSE_API_V1_URL;
import static com.vitamax.util.constants.ServiceConstants.RECOMMENDATION_API_V1_URL;
import static com.vitamax.util.constants.ServiceConstants.REVIEW_API_V1_URL;
import static com.vitamax.api.event.EventConstants.REVIEW_DELETE_OUT_0;
import static com.vitamax.api.event.EventConstants.REVIEW_UPDATE_OUT_0;

@Service
@Slf4j
public class CourseCompositeIntegrationImpl implements CourseCompositeIntegration {
    private final WebClient webClient;
    private final StreamBridge streamBridge;
    private final Scheduler publishScheduler;

    public CourseCompositeIntegrationImpl(
            final WebClient.Builder webClientBuilder,
            final StreamBridge streamBridge,
            final Scheduler publishScheduler) {
        this.streamBridge = streamBridge;
        this.publishScheduler = publishScheduler;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Course> getCourse(final UUID courseId) {
        log.info("getCourse {}", courseId);
        return webClient.get().uri(COURSE_API_V1_URL, courseId).retrieve()
                .onStatus(statusCode -> statusCode.isSameCodeAs(HttpStatusCode.valueOf(404)), response ->
                        response.bodyToMono(String.class)
                                .map(body -> new NotFoundException("Course not found: " + courseId))
                )
                .bodyToMono(Course.class);
    }

    @Override
    public Mono<Void> createCourse(final CourseCreateCommand command) {
        return Mono.fromRunnable(() -> streamBridge.send(COURSE_CREATE_OUT_0, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> updateCourse(final CourseUpdateCommand command) {
        return Mono.fromRunnable(() -> streamBridge.send(COURSE_UPDATE_OUT_0, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> deleteCourse(final UUID courseId) {
        return Mono.fromRunnable(() -> streamBridge.send(COURSE_DELETE_OUT_0, courseId))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Flux<Recommendation> getRecommendations(final UUID courseId) {
        log.info("getRecommendations {}", courseId);
        return webClient.get().uri(RECOMMENDATION_API_V1_URL, courseId).retrieve().bodyToFlux(Recommendation.class);
    }

    @Override
    public Mono<Void> createRecommendation(final RecommendationCreateCommand command) {
        return Mono.fromRunnable(() -> streamBridge.send(RECOMMENDATION_CREATE_OUT_0, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> updateRecommendation(final RecommendationUpdateCommand command) {
        return Mono.fromRunnable(() -> streamBridge.send(RECOMMENDATION_UPDATE_OUT_0, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> deleteRecommendations(final UUID courseId) {
        return Mono.fromRunnable(() -> streamBridge.send(RECOMMENDATION_DELETE_OUT_0, courseId))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Flux<Review> getReviews(final UUID courseId) {
        log.info("getReviews {}", courseId);
        return webClient.get().uri(REVIEW_API_V1_URL, courseId).retrieve().bodyToFlux(Review.class);
    }

    @Override
    public Mono<Void> createReview(final ReviewCreateCommand command) {
        return Mono.fromRunnable(() -> streamBridge.send(REVIEW_CREATE_OUT_0, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> updateReview(final ReviewUpdateCommand command) {
        return Mono.fromRunnable(() -> streamBridge.send(REVIEW_UPDATE_OUT_0, command))
                .subscribeOn(publishScheduler)
                .then();
    }

    @Override
    public Mono<Void> deleteReviews(final UUID courseId) {
        return Mono.fromRunnable(() -> streamBridge.send(REVIEW_DELETE_OUT_0, courseId))
                .subscribeOn(publishScheduler)
                .then();
    }
}
