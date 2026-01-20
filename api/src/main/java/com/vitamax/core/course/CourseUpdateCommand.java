package com.vitamax.core.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CourseUpdateCommand(@NotNull UUID courseId, @NotBlank String name) {
}
