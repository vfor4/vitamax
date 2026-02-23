package com.vitamax.review_service.event;

import com.vitamax.api.core.review.ReviewService;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {

    private final ReviewService reviewService;

    @Bean
    public Consumer<Flux<ReviewCreateCommand>> review() {
        return flux -> flux
                .flatMap(reviewService::createReview)
                .doOnError(e -> log.error("Failed to process review event", e))
                .retry()
                .subscribe();
    }
}