package com.vitamax.core.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/review")
public interface ReviewService {

    @GetMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<List<Review>> getReviews(@PathVariable @Positive int courseId);

    @PostMapping(produces = "application/json")
    ResponseEntity<Review> createReview(@RequestBody @Valid ReviewCreateCommand command);

    @PutMapping(produces = "application/json")
    ResponseEntity<Review> updateReview(@RequestBody @Valid ReviewUpdateCommand command);

    @DeleteMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<Void> deleteReview(@PathVariable @Positive int courseId);
}
