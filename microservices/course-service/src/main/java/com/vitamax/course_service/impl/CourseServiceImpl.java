package com.vitamax.course_service.impl;

import com.vitamax.api.core.course.CourseService;
import com.vitamax.api.core.course.dto.Course;
import com.vitamax.api.core.course.dto.CourseCreateCommand;
import com.vitamax.api.core.course.dto.CourseUpdateCommand;
import com.vitamax.course_service.mapper.CourseMapper;
import com.vitamax.course_service.repository.CourseRepository;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository repository;
    private final ServiceUtil serviceUtil;
    private final CourseMapper mapper;

    @Override
    public Mono<Course> getCourse(final UUID courseId) {
        log.debug("get found course for courseId={}", courseId);

        return repository.findByCourseId(courseId.toString())
                .map(c -> mapper.toCourse(c, serviceUtil.getServiceAddress()));
    }

    @Override
    public Mono<Void> createCourse(final CourseCreateCommand command) {
        log.debug("create course for command={}", command);

        return repository.save(mapper.toEntity(command)).then();
    }

    @Override
    public Mono<Void> updateCourse(final CourseUpdateCommand command) {
        log.debug("update course for command={}", command);

//        return repository.findByCourseId(command.courseId().toString())
//                .map(entity -> ResponseEntity.ok(mapper.toCourse(repository.save(mapper.toEntity(command, entity)), serviceUtil.getServiceAddress())))
//                .orElseThrow(() -> new NotFoundException("course not found"));
        return Mono.empty();
    }

    @Override
    public Mono<Void> deleteCourse(final UUID courseId) {
        log.debug("delete course for courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

//        return ResponseEntity.noContent().build();
        return Mono.empty();
    }
}
