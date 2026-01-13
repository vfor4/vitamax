package com.vitamax.course_service.course;

import com.vitamax.core.course.Course;
import com.vitamax.core.course.CourseService;
import com.vitamax.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CourseServiceImpl implements CourseService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public CourseServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Course getCourse(final int courseId) {
        LOG.debug("get found course for courseId={}", courseId);
        return new Course(courseId, "name-" + courseId, serviceUtil.getServiceAddress());
    }
}
