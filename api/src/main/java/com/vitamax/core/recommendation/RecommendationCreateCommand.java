package com.vitamax.core.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RecommendationCreateCommand(@Positive int courseId, @NotBlank String author, @Positive int rate, @NotBlank String content) {
}
