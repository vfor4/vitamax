package com.vitamax.api.core.recommendation;

import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.recommendation.dto.RecommendationCreateCommand;
import com.vitamax.api.core.recommendation.dto.RecommendationUpdateCommand;
import com.vitamax.api.exception.HttpErrorInfo;
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

import java.util.List;
import java.util.UUID;

@RequestMapping(value = "/api/v1/recommendation", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Recommendation Management", description = "API for managing course recommendations")
public interface RecommendationService {

    @Operation(
            summary = "Get recommendations for a course",
            description = "Retrieves all recommendations associated with a specific course"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recommendations successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Recommendation.class)
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
    ResponseEntity<List<Recommendation>> getRecommendations(
            @Parameter(description = "Unique identifier of the course", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );

    @Operation(
            summary = "Create a new recommendation",
            description = "Creates a new course recommendation"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Recommendation successfully created"
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
    ResponseEntity<Void> createRecommendation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recommendation creation command with all required fields",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecommendationCreateCommand.class))
            )
            @RequestBody @Valid RecommendationCreateCommand command
    );

    @Operation(
            summary = "Update an existing recommendation",
            description = "Updates an existing course recommendation with new information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recommendation successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Recommendation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recommendation not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
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
    @PutMapping
    ResponseEntity<Recommendation> updateRecommendation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recommendation update command with fields to update",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecommendationUpdateCommand.class))
            )
            @RequestBody @Valid RecommendationUpdateCommand command
    );

    @Operation(
            summary = "Delete a recommendation",
            description = "Deletes all recommendations associated with a specific course"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Recommendation successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteRecommendations(
            @Parameter(description = "Unique identifier of the course whose recommendations should be deleted", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );
}
