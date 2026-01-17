package com.vitamax.recommendation_service.recommendation;

import com.vitamax.recommendation_service.recommendation.entities.RecommendationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendationRepository extends MongoRepository<RecommendationEntity, String> {
}
