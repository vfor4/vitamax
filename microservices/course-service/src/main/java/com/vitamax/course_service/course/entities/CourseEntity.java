package com.vitamax.course_service.course.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "course")
public class CourseEntity {

    @Id
    public String id;

    public int courseId;

    public String name;

    public CourseEntity(int courseId, String name) {
        this.courseId = courseId;
        this.name = name;
    }
}
