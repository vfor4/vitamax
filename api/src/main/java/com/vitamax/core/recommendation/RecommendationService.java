package com.vitamax.core.recommendation;

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
public interface RecommendationService {

    @GetMapping(value = "/{courseId}")
    ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable @NotNull UUID courseId);

    @PostMapping
    ResponseEntity<Recommendation> createRecommendation(@RequestBody @Valid RecommendationCreateCommand command);

    @PutMapping
    ResponseEntity<Recommendation> updateRecommendation(@RequestBody @Valid RecommendationUpdateCommand command);

    @DeleteMapping(value = "/{courseId}")
    ResponseEntity<Void> deleteRecommendation(@PathVariable @NotNull UUID courseId);
}
