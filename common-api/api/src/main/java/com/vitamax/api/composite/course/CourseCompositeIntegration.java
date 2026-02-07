package com.vitamax.api.composite.course;

import com.vitamax.api.core.course.CourseService;
import com.vitamax.api.core.recommendation.RecommendationService;
import com.vitamax.api.core.review.ReviewService;

public interface CourseCompositeIntegration extends CourseService, RecommendationService, ReviewService {
}
