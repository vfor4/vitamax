package com.vitamax.api.core.review.dto;

public record Review(String courseId, String reviewId, String author, String subject, String content,
                     String serviceAddress) {
}
