package com.vitamax.core.course;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CourseService {

    @GetMapping(value = "/course/{courseId}", produces = "application/json")
    Course getCourse(@PathVariable int courseId);
}
