package com.vitamax.core.course;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping(value = "/api/v1/course", produces = MediaType.APPLICATION_JSON_VALUE)
public interface CourseService {

    @GetMapping(value = "/{courseId}")
    ResponseEntity<Course> getCourse(@PathVariable @NotNull UUID courseId);

    @PostMapping
    ResponseEntity<Course> createCourse(@RequestBody @Valid CourseCreateCommand command);

    @PutMapping
    ResponseEntity<Course> updateCourse(@RequestBody @Valid CourseUpdateCommand command);

    @DeleteMapping(value = "/{courseId}")
    ResponseEntity<Void> deleteCourse(@PathVariable @NotNull UUID courseId);
}
