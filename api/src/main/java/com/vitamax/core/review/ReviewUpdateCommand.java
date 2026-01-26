package com.vitamax.core.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewUpdateCommand(@NotNull UUID courseId, @NotNull UUID reviewId, @NotBlank String author,
                                  @NotBlank String subject, @NotBlank String content) {
}
