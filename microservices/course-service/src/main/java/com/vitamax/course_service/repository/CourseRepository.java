package com.vitamax.course_service.repository;

import com.vitamax.course_service.entity.CourseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<CourseEntity, String> {
    Optional<CourseEntity> findByCourseId(String courseId);

    void deleteByCourseId(String courseId);
}
