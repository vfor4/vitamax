package com.vitamax.review_service.review;

import com.vitamax.review_service.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, String> {
    List<ReviewEntity> findByCourseId(String courseId);

    void deleteByCourseId(String courseId);
}
