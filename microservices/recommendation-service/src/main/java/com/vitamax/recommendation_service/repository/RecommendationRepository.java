package com.vitamax.recommendation_service.repository;

import com.vitamax.recommendation_service.entity.RecommendationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecommendationRepository extends ReactiveMongoRepository<RecommendationEntity, String> {
    Flux<RecommendationEntity> findByCourseId(final String courseId);

    Mono<RecommendationEntity> findByRecommendationId(final String recommendationId);

    Mono<Void> deleteByCourseId(final String courseId);
}
