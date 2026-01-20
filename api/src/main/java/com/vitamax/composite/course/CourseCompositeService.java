package com.vitamax.composite.course;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/api/v1/aggregate/course")
public interface CourseCompositeService {
    @GetMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<CourseAggregate> getCourseComposite(@PathVariable @NotNull UUID courseId);

    @DeleteMapping(value = "/{courseId}", produces = "application/json")
    ResponseEntity<Void> deleteCourseComposite(@PathVariable @NotNull UUID courseId);
}
