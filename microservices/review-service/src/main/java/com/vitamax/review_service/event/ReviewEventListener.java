package com.vitamax.review_service.event;

import com.vitamax.api.core.review.ReviewService;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {

    private final ReviewService reviewService;

    @Bean
    public Consumer<Flux<ReviewCreateCommand>> reviewCreate() {
        return flux -> flux
                .flatMap(reviewService::createReview)
                .doOnError(e -> log.error("Failed to process review create event", e))
                .retry()
                .subscribe();
    }

    @Bean
    public Consumer<Flux<ReviewUpdateCommand>> reviewUpdate() {
        return flux -> flux
                .flatMap(reviewService::updateReview)
                .doOnError(e -> log.error("Failed to process review update event", e))
                .subscribe();
    }

    @Bean
    public Consumer<Flux<UUID>> reviewDelete() {
        return flux -> flux
                .flatMap(reviewService::deleteReviews)
                .doOnError(e -> log.error("Failed to process review delete event", e))
                .subscribe();
    }
}