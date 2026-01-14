package com.vitamax.composite.course;

import com.vitamax.core.course.Course;
import com.vitamax.core.recommendation.Recommendation;
import com.vitamax.core.review.Review;

import java.util.List;

public record CourseAggregate(Course course, List<Recommendation> recommendations, List<Review> reviews) {
}
