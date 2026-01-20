package com.vitamax.core.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseCreateCommand(@NotBlank @Size(min = 1, max = 100) String name) {
}
