package com.vitamax.core.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewCreateCommand(@NotNull UUID courseId, @NotBlank String author, @NotBlank String subject,
                                  @NotBlank String content) {
}
