package com.vitamax.course_service.course.entities;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "course")
@Getter
@Builder
public class CourseEntity {

    @Id
    String id;

    @Indexed(unique = true)
    String courseId;

    @Size(min = 1, max = 100)
    String name;
}
