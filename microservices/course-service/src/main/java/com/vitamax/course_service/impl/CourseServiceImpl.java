package com.vitamax.course_service.impl;

import com.vitamax.api.core.course.CourseService;
import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.api.exception.dto.NotFoundException;
import com.vitamax.api.util.ApiUtil;
import com.vitamax.course_service.mapper.CourseMapper;
import com.vitamax.course_service.repository.CourseRepository;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository repository;
    private final ServiceUtil serviceUtil;
    private final CourseMapper mapper;

    @Override
    public ResponseEntity<Course> getCourse(final UUID courseId) {
        log.debug("get found course for courseId={}", courseId);

        return repository.findByCourseId(courseId.toString())
                .map(c -> ResponseEntity.ok(mapper.toCourse(c, serviceUtil.getServiceAddress())))
                .orElseThrow(() -> new NotFoundException("course not found"));
    }

    @Override
    public ResponseEntity<Void> createCourse(final CourseCreateCommand command) {
        log.debug("create course for command={}", command);

        return ResponseEntity.created(ApiUtil.buildCreatedLocation(repository.save(mapper.toEntity(command)).getCourseId())).build();
    }

    @Override
    public ResponseEntity<Course> updateCourse(final CourseUpdateCommand command) {
        log.debug("update course for command={}", command);

        return repository.findByCourseId(command.courseId().toString())
                .map(entity -> ResponseEntity.ok(mapper.toCourse(repository.save(mapper.toEntity(command, entity)), serviceUtil.getServiceAddress())))
                .orElseThrow(() -> new NotFoundException("course not found"));

    }

    @Override
    public ResponseEntity<Void> deleteCourse(final UUID courseId) {
        log.debug("delete course for courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

        return ResponseEntity.noContent().build();
    }
}
