package com.vitamax.course_service.course;

import com.vitamax.course_service.course.entities.CourseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<CourseEntity, String> {
}
