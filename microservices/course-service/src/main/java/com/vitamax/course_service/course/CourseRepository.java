package com.vitamax.course_service.course;

import com.vitamax.course_service.course.entities.CourseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<CourseEntity, String> {
    Optional<CourseEntity> findByCourseId(String courseId);

    boolean existsByCourseId(String courseId);

    void deleteByCourseId(String courseId);
}
