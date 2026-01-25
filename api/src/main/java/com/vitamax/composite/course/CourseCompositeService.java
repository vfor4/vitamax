package com.vitamax.composite.course;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(value = "/api/v1/aggregate/course", produces = MediaType.APPLICATION_JSON_VALUE)
public interface CourseCompositeService {
    @GetMapping(value = "/{courseId}")
    ResponseEntity<CourseAggregate> getCourseComposite(@PathVariable @NotNull UUID courseId);

    @DeleteMapping(value = "/{courseId}")
    ResponseEntity<Void> deleteCourseComposite(@PathVariable @NotNull UUID courseId);

    @PostMapping(produces = "application/json")
    ResponseEntity<Void> createCourseComposite(@RequestBody CourseAggregateCreateCommand createCommand);
}
