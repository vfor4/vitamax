package com.vitamax.api.composite.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.util.List;

public record CourseAggregateCreateCommand(@NotNull CourseCreateCommand course,
                                           List<RecommendationCreateCommand> recommendations,
                                           List<ReviewCreateCommand> reviews) implements Serializable {

    public record CourseCreateCommand(@NotBlank String name) implements Serializable {
    }

    public record RecommendationCreateCommand(@NotBlank String author, @Positive int rate,
                                              @NotBlank String content) implements Serializable {
    }

    public record ReviewCreateCommand(@NotBlank String author, @NotBlank String subject,
                                      @NotBlank String content) implements Serializable {
    }

}
