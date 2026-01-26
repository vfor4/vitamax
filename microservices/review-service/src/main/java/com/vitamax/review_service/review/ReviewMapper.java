package com.vitamax.review_service.review;

import com.vitamax.core.review.Review;
import com.vitamax.core.review.ReviewCreateCommand;
import com.vitamax.core.review.ReviewUpdateCommand;
import com.vitamax.review_service.review.enities.ReviewEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "serviceAddress", expression = "java(serviceAddress)")
    Review toReview(final ReviewEntity review, @Context final String serviceAddress);

    List<Review> toReviews(final List<ReviewEntity> reviews, @Context final String serviceAddress);

    @Mapping(target = "reviewId", expression = "java(java.util.UUID.randomUUID().toString())")
    ReviewEntity toEntity(final ReviewCreateCommand command);

    ReviewEntity toEntity(final ReviewUpdateCommand command);

}
