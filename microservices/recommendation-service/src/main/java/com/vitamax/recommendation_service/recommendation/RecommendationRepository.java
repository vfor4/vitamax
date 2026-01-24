package com.vitamax.recommendation_service.recommendation;

import com.vitamax.recommendation_service.recommendation.entities.RecommendationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecommendationRepository extends MongoRepository<RecommendationEntity, String> {
    List<RecommendationEntity> findByCourseId(final String courseId);

    boolean existsByRecommendationId(final String recommendationId);

    void deleteByCourseId(final String courseId);
}
