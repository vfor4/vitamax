package com.vitamax.course_service.mapper;

import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.course_service.entity.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {
    @Mapping(target = "serviceAddress", source = "serviceAddress")
    Course toCourse(final CourseEntity entity, final String serviceAddress);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "courseId", expression = "java(java.util.UUID.randomUUID().toString())")
    CourseEntity toEntity(final CourseCreateCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "version", ignore = true)
    CourseEntity toEntity(final CourseUpdateCommand command, @MappingTarget CourseEntity entity);
}
