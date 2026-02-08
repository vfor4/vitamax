package com.vitamax.recommendation_service.repository;

import com.vitamax.recommendation_service.entity.RecommendationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface RecommendationRepository extends ReactiveMongoRepository<RecommendationEntity, String> {
    Flux<RecommendationEntity> findByCourseId(final String courseId);

    Optional<RecommendationEntity> findByRecommendationId(final String recommendationId);

    void deleteByCourseId(final String courseId);
}
