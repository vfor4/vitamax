package com.vitamax.core.course;

import jakarta.validation.constraints.NotBlank;

public record CourseCreateCommand(@NotBlank String name) {
}
