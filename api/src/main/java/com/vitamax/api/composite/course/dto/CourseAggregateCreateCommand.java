package com.vitamax.api.composite.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CourseAggregateCreateCommand(@NotNull CourseCreateCommand course,
                                           List<RecommendationCreateCommand> recommendations,
                                           List<ReviewCreateCommand> reviews) {

    public record CourseCreateCommand(@NotBlank String name) {
    }

    public record RecommendationCreateCommand(@NotBlank String author, @Positive int rate, @NotBlank String content) {
    }

    public record ReviewCreateCommand(@NotBlank String author, @NotBlank String subject, @NotBlank String content) {
    }

}
