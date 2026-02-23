package com.vitamax.course_service.repository;

import com.vitamax.course_service.entity.CourseEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CourseRepository extends ReactiveMongoRepository<CourseEntity, String> {
    Mono<CourseEntity> findByCourseId(String courseId);

    Mono<Void> deleteByCourseId(String courseId);
}
