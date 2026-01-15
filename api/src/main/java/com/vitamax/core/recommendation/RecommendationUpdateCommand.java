package com.vitamax.core.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RecommendationUpdateCommand(@Positive int courseId, @Positive int recommendationId, @NotBlank String author, @Positive int rate, @NotBlank String content) {
}
