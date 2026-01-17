package com.vitamax.review_service.review;

import com.vitamax.review_service.review.enities.ReviewEntity;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
}
