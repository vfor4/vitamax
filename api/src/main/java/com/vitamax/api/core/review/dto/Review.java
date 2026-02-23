package com.vitamax.api.core.review.dto;

import java.io.Serializable;

public record Review(String courseId, String reviewId, String author, String subject, String content,
                     String serviceAddress) implements Serializable {
}
