package com.vitamax.api.core.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

public record CourseCreateCommand(@NotNull UUID courseId,
                                  @NotBlank @Size(min = 1, max = 100) String name) implements Serializable {
}
