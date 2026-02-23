package com.vitamax.recommendation_service.event;

import com.vitamax.api.core.recommendation.RecommendationService;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RecommendationEventListener {

    private final RecommendationService recommendationService;

    @Bean
    public Consumer<Flux<RecommendationCreateCommand>> recommendation() {
        return flux -> flux
                .flatMap(recommendationService::createRecommendation)
                .doOnError(e -> log.error("Failed to process recommendation event", e))
                .retry()
                .subscribe();
    }
}
