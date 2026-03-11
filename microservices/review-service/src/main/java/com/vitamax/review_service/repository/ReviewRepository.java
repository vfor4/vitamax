package com.vitamax.review_service.repository;

import com.vitamax.review_service.entity.ReviewEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository extends ReactiveCrudRepository<ReviewEntity, String> {
    Flux<ReviewEntity> findByCourseId(String courseId);

    Mono<Void> deleteByCourseId(String courseId);
}
