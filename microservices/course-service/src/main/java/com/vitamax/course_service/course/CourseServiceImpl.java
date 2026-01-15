package com.vitamax.course_service.course;

import com.vitamax.core.course.Course;
import com.vitamax.core.course.CourseCreateCommand;
import com.vitamax.core.course.CourseService;
import com.vitamax.core.course.CourseUpdateCommand;
import com.vitamax.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CourseServiceImpl implements CourseService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public CourseServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ResponseEntity<Course> getCourse(final int courseId) {
        LOG.debug("get found course for courseId={}", courseId);
        return ResponseEntity.ok(new Course(courseId, "name-" + courseId, serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Course> createCourse(final CourseCreateCommand command) {
        LOG.debug("create course for command={}", command);
        return ResponseEntity.ok(new Course(1, command.name(), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Course> updateCourse(final CourseUpdateCommand command) {
        LOG.debug("update course for command={}", command);
        return ResponseEntity.ok(new Course(command.courseId(), command.name(), serviceUtil.getServiceAddress()));
    }

    @Override
    public ResponseEntity<Void> deleteCourse(final int courseId) {
        LOG.debug("delete course for courseId={}", courseId);
        return ResponseEntity.noContent().build();
    }
}
