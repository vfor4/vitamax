package com.vitamax.course_service.course;

import com.vitamax.core.course.Course;
import com.vitamax.core.course.CourseCreateCommand;
import com.vitamax.core.course.CourseUpdateCommand;
import com.vitamax.course_service.course.entities.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "serviceAddress", source = "serviceAddress")
    Course toCourse(final CourseEntity entity, final String serviceAddress);

    @Mapping(target = "courseId", expression = "java(java.util.UUID.randomUUID().toString())")
    CourseEntity toCourseEntity(final CourseCreateCommand command);

    CourseEntity toCourseEntity(final CourseUpdateCommand command);
}
