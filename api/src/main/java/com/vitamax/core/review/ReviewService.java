package com.vitamax.core.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ReviewService {

    @GetMapping(value = "/review/{courseId}", produces = "application/json")
    List<Review> getReviews(@PathVariable int courseId);
}
