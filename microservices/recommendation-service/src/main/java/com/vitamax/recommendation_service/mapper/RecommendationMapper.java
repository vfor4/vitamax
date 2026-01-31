package com.vitamax.recommendation_service.mapper;

import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.recommendation_service.entity.RecommendationEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "serviceAddress", expression = "java(serviceAddress)")
    Recommendation toRecommendation(final RecommendationEntity recommendation, @Context final String serviceAddress);

    List<Recommendation> toRecommendations(final List<RecommendationEntity> recommendations, @Context final String serviceAddress);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "recommendationId", expression = "java(java.util.UUID.randomUUID().toString())")
    RecommendationEntity toEntity(final RecommendationCreateCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "recommendationId", ignore = true)
    @Mapping(target = "version", ignore = true)
    RecommendationEntity toEntity(final RecommendationUpdateCommand command, @MappingTarget RecommendationEntity entity);
}
