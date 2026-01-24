package com.vitamax.core.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record RecommendationUpdateCommand(@NotNull UUID courseId, @NotNull UUID recommendationId,
                                          @NotBlank String author, @Positive int rate, @NotBlank String content) {
}
