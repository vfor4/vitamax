package com.vitamax.review_service.review;

import com.vitamax.review_service.review.enities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, String> {
    List<ReviewEntity> findByCourseId(String courseId);

    boolean existsByReviewId(String reviewId);

    void deleteByCourseId(String courseId);
}
