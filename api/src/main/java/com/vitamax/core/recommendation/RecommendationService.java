package com.vitamax.core.recommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RecommendationService {

    @GetMapping(value = "/recommendation/{courseId}", produces = "application/json")
    List<Recommendation> getRecommendations(@PathVariable int courseId);
}
