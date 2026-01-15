package com.vitamax.core.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ReviewCreateCommand(@Positive int courseId, @NotBlank String author, @NotBlank String subject, @NotBlank String content) {
}
