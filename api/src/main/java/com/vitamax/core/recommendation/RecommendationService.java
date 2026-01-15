package com.vitamax.core.recommendation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/recommendation")
public interface RecommendationService {

    @GetMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable int courseId);

    @PostMapping(produces = "application/json")
    ResponseEntity<Recommendation> createRecommendation(@RequestBody @Valid RecommendationCreateCommand command);

    @PutMapping(produces = "application/json")
    ResponseEntity<Recommendation> updateRecommendation(@RequestBody @Valid RecommendationUpdateCommand command);

    @DeleteMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<Void> deleteRecommendation(@PathVariable int courseId);
}
