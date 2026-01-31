package com.vitamax.api.core.review;

import com.vitamax.api.core.review.dto.Review;
import com.vitamax.api.core.review.dto.ReviewCreateCommand;
import com.vitamax.api.core.review.dto.ReviewUpdateCommand;
import com.vitamax.api.exception.dto.HttpErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@RequestMapping(value = "/api/v1/review", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Review", description = "Course review management APIs")
public interface ReviewService {

    @Operation(
            summary = "Get all reviews for a course",
            description = "Retrieves a list of all reviews associated with the specified course ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved reviews",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Review.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid course ID format",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @GetMapping("/{courseId}")
    ResponseEntity<List<Review>> getReviews(
            @Parameter(description = "UUID of the course", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );

    @Operation(
            summary = "Create a new review",
            description = "Creates a new review for a course"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Review created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Review.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @PostMapping
    ResponseEntity<Void> createReview(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Review creation data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewCreateCommand.class))
            )
            @RequestBody @Valid ReviewCreateCommand command
    );

    @Operation(
            summary = "Update an existing review",
            description = "Updates an existing review with new information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Review.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @PutMapping
    ResponseEntity<Review> updateReview(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Review update data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewUpdateCommand.class))
            )
            @RequestBody @Valid ReviewUpdateCommand command
    );

    @Operation(
            summary = "Delete all reviews for a course",
            description = "Deletes all reviews associated with the specified course ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Reviews deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server has error",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfo.class))
            )
    })
    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteReviews(
            @Parameter(description = "UUID of the course", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull UUID courseId
    );
}
