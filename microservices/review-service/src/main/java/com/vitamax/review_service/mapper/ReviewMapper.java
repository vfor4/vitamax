package com.vitamax.review_service.mapper;

import com.vitamax.api.core.review.dto.Review;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.review_service.entity.ReviewEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "serviceAddress", expression = "java(serviceAddress)")
    Review toReview(final ReviewEntity review, @Context final String serviceAddress);

    List<Review> toReviews(final List<ReviewEntity> reviews, @Context final String serviceAddress);

    @Mapping(target = "reviewId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "version", ignore = true)
    ReviewEntity toEntity(final ReviewCreateCommand command);

    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "version", ignore = true)
    ReviewEntity toEntity(final ReviewUpdateCommand command, @MappingTarget ReviewEntity entity);
}
