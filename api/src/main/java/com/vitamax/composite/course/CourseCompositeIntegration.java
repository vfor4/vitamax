package com.vitamax.composite.course;

import com.vitamax.core.course.CourseService;
import com.vitamax.core.recommendation.RecommendationService;
import com.vitamax.core.review.ReviewService;

public interface CourseCompositeIntegration extends CourseService, RecommendationService, ReviewService {
}
