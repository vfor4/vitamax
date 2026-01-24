package com.vitamax.recommendation_service.recommendation;

import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.recommendation.RecommendationCreateCommand;
import com.vitamax.core.recommendation.RecommendationUpdateCommand;
import com.vitamax.recommendation_service.recommendation.entities.RecommendationEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "serviceAddress", expression = "java(serviceAddress)")
    Recommendation toRecommendation(final RecommendationEntity recommendation, @Context final String serviceAddress);

    List<Recommendation> toRecommendations(final List<RecommendationEntity> recommendations, @Context final String serviceAddress);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recommendationId", expression = "java(java.util.UUID.randomUUID().toString())")
    RecommendationEntity toEntity(final RecommendationCreateCommand command);

    @Mapping(target = "id", ignore = true)
    RecommendationEntity toEntity(final RecommendationUpdateCommand command);

}
