package com.vitamax.course_service.course;

import com.vitamax.core.course.Course;
import com.vitamax.core.course.CourseCreateCommand;
import com.vitamax.core.course.CourseService;
import com.vitamax.core.course.CourseUpdateCommand;
import com.vitamax.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Course> createCourse(final CourseCreateCommand command) {
        log.debug("create course for command={}", command);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCourse(repository.save(mapper.toCourseEntity(command)), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Course> updateCourse(final CourseUpdateCommand command) {
        log.debug("update course for command={}", command);

        if (!repository.existsByCourseId(command.courseId().toString())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toCourse(repository.save(mapper.toCourseEntity(command)), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> deleteCourse(final UUID courseId) {
        log.debug("delete course for courseId={}", courseId);

        repository.deleteByCourseId(courseId.toString());

        return ResponseEntity.noContent().build();
    }
}
