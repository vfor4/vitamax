package com.vitamax.core.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CourseUpdateCommand(@Positive int courseId, @NotBlank String name) {
}
