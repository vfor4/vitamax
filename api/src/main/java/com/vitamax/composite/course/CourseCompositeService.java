package com.vitamax.composite.course;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CourseCompositeService {
    @GetMapping(value = "/aggregate/course/{courseId}", produces = "application/json")
    CourseAggregate getCourseComposite(@PathVariable int courseId);
}
