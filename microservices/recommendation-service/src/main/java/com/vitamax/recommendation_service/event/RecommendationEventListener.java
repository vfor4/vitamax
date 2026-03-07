package com.vitamax.recommendation_service.event;

import com.vitamax.api.core.recommendation.RecommendationService;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
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
public class RecommendationEventListener {

    private final RecommendationService recommendationService;

    @Bean
    public Consumer<Flux<RecommendationCreateCommand>> recommendationCreate() {
        return flux -> flux
                .flatMap(recommendationService::createRecommendation)
                .doOnError(e -> log.error("Failed to process recommendation create event", e))
                .retry()
                .subscribe();
    }

    @Bean
    public Consumer<Flux<RecommendationUpdateCommand>> recommendationUpdate() {
        return flux -> flux
                .flatMap(recommendationService::updateRecommendation)
                .doOnError(e -> log.error("Failed to process recommendation update event", e))
                .subscribe();
    }

    @Bean
    public Consumer<Flux<UUID>> recommendationDelete() {
        return flux -> flux
                .flatMap(recommendationService::deleteRecommendations)
                .doOnError(e -> log.error("Failed to process recommendation delete event", e))
                .subscribe();
    }
}
