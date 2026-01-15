package com.vitamax.core.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ReviewUpdateCommand(@Positive int courseId, @Positive int reviewId, @NotBlank String author, @NotBlank String subject, @NotBlank String content) {
}
