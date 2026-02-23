package com.vitamax.api.composite.course.dto;

import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.recommendation.dto.Recommendation;
import com.vitamax.api.core.review.dto.Review;

import java.io.Serializable;
import java.util.List;

public record CourseAggregate(Course course, List<Recommendation> recommendations,
                              List<Review> reviews) implements Serializable {
}
