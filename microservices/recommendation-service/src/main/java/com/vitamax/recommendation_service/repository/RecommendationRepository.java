package com.vitamax.recommendation_service.repository;

import com.vitamax.recommendation_service.entity.RecommendationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends MongoRepository<RecommendationEntity, String> {
    List<RecommendationEntity> findByCourseId(final String courseId);

    Optional<RecommendationEntity> findByRecommendationId(final String recommendationId);

    void deleteByCourseId(final String courseId);
}
