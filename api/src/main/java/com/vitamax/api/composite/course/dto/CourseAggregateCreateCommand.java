package com.vitamax.api.composite.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.util.List;

public record CourseAggregateCreateCommand(@NotNull @Valid CourseCreateCommand course,
                                           @Valid List<RecommendationCreateCommand> recommendations,
                                           @Valid List<ReviewCreateCommand> reviews) implements Serializable {

    public record CourseCreateCommand(@NotBlank String name) implements Serializable {
    }

    public record RecommendationCreateCommand(@NotBlank String author, @PositiveOrZero int rate,
                                              @NotBlank String content) implements Serializable {
    }

    public record ReviewCreateCommand(@NotBlank String author, @NotBlank String subject,
                                      @NotBlank String content) implements Serializable {
    }

}
