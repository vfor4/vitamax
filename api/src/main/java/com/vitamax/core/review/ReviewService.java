package com.vitamax.core.review;

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
public interface ReviewService {

    @GetMapping("/{courseId}")
    ResponseEntity<List<Review>> getReviews(@PathVariable @NotNull UUID courseId);

    @PostMapping
    ResponseEntity<Review> createReview(@RequestBody @Valid ReviewCreateCommand command);

    @PutMapping
    ResponseEntity<Review> updateReview(@RequestBody @Valid ReviewUpdateCommand command);

    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteReviews(@PathVariable @NotNull UUID courseId);
}
