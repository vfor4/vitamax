package com.vitamax.api.core.course;

import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.api.exception.dto.HttpErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping(value = "/api/v1/course", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Course Management", description = "API for managing individual courses")
public interface CourseService {

    @Operation(
            summary = "Get course by ID",
            description = "Retrieves a course by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Course.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid course ID format",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @GetMapping("/{courseId}")
    ResponseEntity<Course> getCourse(
            @Parameter(description = "Unique identifier of the course", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );

    @Operation(
            summary = "Create a new course",
            description = "Creates a new course with the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Course successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Course.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @PostMapping
    ResponseEntity<Void> createCourse(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Course creation command with all required fields",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CourseCreateCommand.class))
            )
            @RequestBody @Valid CourseCreateCommand command
    );

    @Operation(
            summary = "Update an existing course",
            description = "Updates an existing course with the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Course.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @PutMapping
    ResponseEntity<Course> updateCourse(
            @Parameter(description = "Course update command with fields to update", required = true)
            @RequestBody @Valid CourseUpdateCommand command
    );

    @Operation(
            summary = "Delete a course",
            description = "Deletes a course by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Course successfully deleted"
            )
    })
    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Unique identifier of the course to delete", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );
}
