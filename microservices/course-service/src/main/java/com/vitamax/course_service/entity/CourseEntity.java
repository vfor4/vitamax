package com.vitamax.course_service.entity;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "course")
@Getter
@Setter
public class CourseEntity {

    @Id
    @Size(min = 36, max = 36)
    String id;

    @Indexed(unique = true)
    @Size(min = 36, max = 36)
    String courseId;

    @Size(min = 1, max = 255)
    String name;

    @Version
    private Integer version;

    public CourseEntity(String courseId, String name) {
        this.courseId = courseId;
        this.name = name;
    }
}
