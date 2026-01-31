package com.vitamax.api.composite.course;

import com.vitamax.api.composite.course.dto.CourseAggregate;
import com.vitamax.api.composite.course.dto.CourseAggregateCreateCommand;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping(value = "/api/v1/aggregate/course", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Course Composite", description = "API for managing course aggregates including all related course data")
public interface CourseCompositeService {
    @Operation(
            summary = "Get course composite by ID",
            description = "Retrieves a complete course aggregate including all associated data for the specified course ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course composite successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseAggregate.class)
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
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @GetMapping("/{courseId}")
    ResponseEntity<CourseAggregate> getCourseComposite(
            @Parameter(description = "Unique identifier of the course", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );

    @Operation(
            summary = "Delete course composite",
            description = "Deletes a course composite and all associated data for the specified course ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Course composite successfully deleted"
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
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteCourseComposite(
            @Parameter(description = "Unique identifier of the course to delete", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );

    @Operation(
            summary = "Create course composite",
            description = "Creates a new course composite with all associated data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Course composite successfully created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @PostMapping
    ResponseEntity<Void> createCourseComposite(
            @Parameter(description = "Course creation command containing all required course data", required = true)
            @RequestBody @Valid CourseAggregateCreateCommand createCommand
    );
}
