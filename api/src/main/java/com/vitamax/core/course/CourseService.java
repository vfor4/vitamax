package com.vitamax.core.course;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/course")
public interface CourseService {

    @GetMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<Course> getCourse(@PathVariable @Positive int courseId);

    @PostMapping(produces = "application/json")
    ResponseEntity<Course> createCourse(@RequestBody @Valid CourseCreateCommand command);

    @PutMapping(produces = "application/json")
    ResponseEntity<Course> updateCourse(@RequestBody @Valid CourseUpdateCommand command);

    @DeleteMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<Void> deleteCourse(@PathVariable @Positive int courseId);
}
