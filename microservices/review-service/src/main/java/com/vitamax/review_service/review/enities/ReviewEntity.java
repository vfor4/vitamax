package com.vitamax.review_service.review.enities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "review")
public class ReviewEntity {
    private int courseId;

    @Id
    private Integer id;
    private String author;
    private String subject;
    private String content;
}
