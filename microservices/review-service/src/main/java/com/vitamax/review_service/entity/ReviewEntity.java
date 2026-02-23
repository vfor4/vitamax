package com.vitamax.review_service.entity;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ReviewEntity {
    @Id
    @Column("id")
    @Size(min = 36, max = 36)
    private String reviewId;

    @Column("course_id")
    @Size(min = 36, max = 36)
    private String courseId;

    @Size(min = 1, max = 255)
    private String author;

    @Size(min = 1, max = 255)
    private String subject;

    private String content;

    @Version
    private Integer version;

    public ReviewEntity(String reviewId, String courseId, String author, String subject, String content) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }
}
