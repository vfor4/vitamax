package com.vitamax.course_service.course;

import com.vitamax.core.course.Course;
import com.vitamax.core.course.CourseCreateCommand;
import com.vitamax.core.course.CourseUpdateCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {
    @Mapping(target = "serviceAddress", source = "serviceAddress")
    Course toCourse(final CourseEntity entity, final String serviceAddress);

    @Mapping(target = "courseId", expression = "java(java.util.UUID.randomUUID().toString())")
    CourseEntity toEntity(final CourseCreateCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseId", ignore = true)
    CourseEntity toEntity(final CourseUpdateCommand command, @MappingTarget CourseEntity entity);
}
