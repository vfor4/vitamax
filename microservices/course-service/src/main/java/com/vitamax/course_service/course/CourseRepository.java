package com.vitamax.course_service.course;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<CourseEntity, String> {
    Optional<CourseEntity> findByCourseId(String courseId);

    void deleteByCourseId(String courseId);
}
