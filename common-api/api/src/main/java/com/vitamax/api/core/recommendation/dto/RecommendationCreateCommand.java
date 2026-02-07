package com.vitamax.api.core.recommendation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record RecommendationCreateCommand(@NotNull UUID courseId, @NotBlank String author, @PositiveOrZero int rate,
                                          @NotBlank String content) {
}
