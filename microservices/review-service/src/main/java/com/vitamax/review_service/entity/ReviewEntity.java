package com.vitamax.review_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "review")
@Getter
@Setter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ReviewEntity {
    @Id
    @Column(name = "id", updatable = false)
    @Size(min = 36, max = 36)
    private String reviewId;

    @Column(updatable = false)
    @Size(min = 36, max = 36)
    private String courseId;

    @Size(min = 1, max = 255)
    private String author;

    @Size(min = 1, max = 255)
    private String subject;

    private String content;

    @Version
    @Column(nullable = false)
    private Integer version;

    public ReviewEntity(String reviewId, String courseId, String author, String subject, String content) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }
}
